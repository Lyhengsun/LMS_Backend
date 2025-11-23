package com.norton.lms_backend.service.impl;

import com.norton.lms_backend.exception.BadRequestException;
import com.norton.lms_backend.exception.NotFoundException;
import com.norton.lms_backend.job.ForceSubmitQuizJob;
import com.norton.lms_backend.model.dto.request.AnswerRequest;
import com.norton.lms_backend.model.dto.request.QuestionRequest;
import com.norton.lms_backend.model.dto.request.QuizRequest;
import com.norton.lms_backend.model.dto.response.*;
import com.norton.lms_backend.model.entity.*;
import com.norton.lms_backend.model.enumeration.CourseLevel;
import com.norton.lms_backend.model.enumeration.QuestionType;
import com.norton.lms_backend.model.enumeration.QuizProperty;
import com.norton.lms_backend.repository.*;
import com.norton.lms_backend.repository.specification.QuizSpecification;
import com.norton.lms_backend.service.CategoryService;
import com.norton.lms_backend.service.LeaderboardService;
import com.norton.lms_backend.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.quartz.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;
    private final CategoryService categoryService;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final TakeQuizRepository takeQuizRepository;
    private final LeaderboardService leaderboardService;
    private final UserLearningStreakRespository userLearningStreakRespository;
    private final Scheduler scheduler;

    /**
     * Schedules a job to force submit the quiz at the deadline
     */
    private void scheduleForceSubmitJob(TakeQuiz takeQuiz) {
        try {
            String jobId = "forceSubmit-" + takeQuiz.getUser().getId() + "-" + takeQuiz.getQuiz().getId() + "-" + takeQuiz.getId();

            JobDetail jobDetail = JobBuilder.newJob(ForceSubmitQuizJob.class)
                    .withIdentity(jobId)
                    .usingJobData("userId", takeQuiz.getUser().getId())
                    .usingJobData("quizId", takeQuiz.getQuiz().getId())
                    .build();

            // Convert LocalDateTime to Date for Quartz trigger
            Date deadlineDate = Date.from(takeQuiz.getDeadlineTime()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toInstant());

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("forceSubmitTrigger-" + jobId)
                    .startAt(deadlineDate)
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);

            log.info("Scheduled force submit job for takeQuizId: {} at {}",
                    takeQuiz.getId(), takeQuiz.getDeadlineTime());

        } catch (SchedulerException e) {
            log.error("Failed to schedule force submit job for takeQuizId: {}", takeQuiz.getId(), e);
            // Don't throw exception - quiz session is already created, job scheduling is non-critical
        }
    }


    private AppUser getCurrentUser() {
        return (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public Quiz findQuizById(Long id) {
        return quizRepository.findById(id).orElseThrow(() -> new NotFoundException("Quiz of id: " + id + " not found"));
    }

    public Question findQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Question of id: " + id + " not found"));
    }

    public Answer findAnswerById(Long id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Answer od id: " + id + " not found"));
    }

    @Override
    public QuizResponse createQuiz(QuizRequest quizRequest) {
        if (quizRequest.getQuestions().isEmpty()) {
            throw new BadRequestException("A Quiz need to have at least one question");
        }

        Quiz quiz = quizRequest.toEntity();
        quiz.setAuthor(getCurrentUser());
        Category category = categoryService.getCategory(quizRequest.getCategoryId());
        quiz.setCategory(category);

        List<Question> questions = quizRequest.getQuestions().stream().map(q -> {
            verifyQuestionRequest(q);
            Question question = q.toEntity(quiz);
            question.setAnswers(q.getAnswers().stream().map(a -> a.toEntity(question)).collect(Collectors.toList()));
            return question;
        }).collect(Collectors.toList());

        quiz.setQuestions(questions);

        return quizRepository.save(quiz).toResponse();
    }

    @Override
    public PagedResponse<QuizNoQuestionResponse> getAllQuizzes(
            Integer page,
            Integer size,
            String name,
            Long categoryId,
            CourseLevel level,
            QuizProperty quizProperty,
            Sort.Direction direction
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, quizProperty.getValue()));

        Specification<Quiz> spec = Specification.unrestricted();

        if (name != null && !name.isEmpty()) {
            spec = spec.and(QuizSpecification.quizNameContains(name));
        }

        if (categoryId != null) {
            spec = spec.and(QuizSpecification.hasCategoryId(categoryId));
        }

        if (level != null) {
            spec = spec.and(QuizSpecification.hasLevel(level));
        }

        Page<Quiz> result = quizRepository.findAll(spec, pageable);
        return PagedResponse.<QuizNoQuestionResponse>builder()
                .items(result.getContent().stream().map(q -> {
                    QuizNoQuestionResponse response = q.toNoQuestionResponse();
                    if (getCurrentUser().getRole().getRoleName().equals("ROLE_STUDENT")) {
                        Integer attempts = takeQuizRepository.countTakeQuizByUserAndQuiz(getCurrentUser(), q);
                        response.setAttemptCount(attempts);
                    }
                    return response;
                }).toList())
                .pagination(new PaginationInfo(result))
                .build();
    }


    @Override
    public QuizResponse getQuizById(Long id) {
        return quizRepository.findById(id).orElseThrow(() -> new NotFoundException("Quiz of id " + id + " not found"))
                .toResponse();
    }

    @Override
    public QuizResponse updateQuiz(Long id, QuizRequest quizRequest) {
        if (quizRequest.getQuestions().size() <= 0) {
            throw new BadRequestException("A Quiz need to have at least one question");
        }

        Quiz quiz = findQuizById(id); // existing entity from DB

        List<Question> questions = quizRequest.getQuestions().stream().map(q -> {
            verifyQuestionRequest(q);
            Question question = q.toEntity(quiz);
            if (q.getAnswers() != null) {
                question.setAnswers(
                        q.getAnswers().stream().map(a -> a.toEntity(question)).collect(Collectors.toList()));
            }
            return question;
        }).collect(Collectors.toList());

        // Update fields manually
        quiz.setQuizName(quizRequest.getQuizName());
        quiz.setQuizDescription(quizRequest.getQuizDescription());
        quiz.setQuizInstruction(quizRequest.getQuizInstruction());
        quiz.setLevel(quizRequest.getLevel());
        quiz.setDurationMinutes(quizRequest.getDurationMinutes());
        quiz.setMaxAttempts(quizRequest.getMaxAttempts());

        // Set category
        Category category = categoryService.getCategory(quizRequest.getCategoryId());
        quiz.setCategory(category);

        // delete all old question
        questionRepository.deleteAllByQuizId(quiz.getId());
        questionRepository.flush();

        quiz.setQuestions(questions);

        // Save updated entity
        return quizRepository.save(quiz).toResponse();
    }

    private void cancelForceSubmitJob(TakeQuiz takeQuiz) {
        try {
            String jobId = "forceSubmit-" + takeQuiz.getUser().getId() + "-" + takeQuiz.getQuiz().getId() + "-" + takeQuiz.getId();
            scheduler.deleteJob(org.quartz.JobKey.jobKey(jobId));
            log.info("Cancelled force submit job for takeQuizId: {}", takeQuiz.getId());
        } catch (SchedulerException e) {
            log.warn("Failed to cancel force submit job for takeQuizId: {}", takeQuiz.getId(), e);
        }
    }

    @Override
    public void deleteQuiz(Long id) {
        Quiz quiz = findQuizById(id);
        if (!getCurrentUser().getId().equals(quiz.getAuthor().getId()))
            throw new BadRequestException("Only the author can delete his/her own quiz");
        quizRepository.delete(quiz);
    }

    @Override
    public PagedResponse<QuizNoQuestionResponse> getAllYourQuizzes(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Quiz> result = quizRepository.findAllByAuthorId(getCurrentUser().getId(), pageable);
        return PagedResponse.<QuizNoQuestionResponse>builder()
                .items(result.getContent().stream().map(Quiz::toNoQuestionResponse).toList())
                .pagination(new PaginationInfo(result))
                .build();
    }

    @Override
    public PagedResponse<QuizNoQuestionResponse> getAllQuizzesByAuthor(Integer page, Integer size, String name) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Specification<Quiz> spec = Specification.unrestricted();
        spec = spec.and(QuizSpecification.hasAuthorId(getCurrentUser().getId()));

        if (name != null && !name.isEmpty()) {
            spec = spec.and(QuizSpecification.quizNameContains(name));
        }

        Page<Quiz> result = quizRepository.findAll(spec, pageable);
        return PagedResponse.<QuizNoQuestionResponse>builder()
                .items(result.getContent().stream().map(Quiz::toNoQuestionResponse).toList())
                .pagination(new PaginationInfo(result))
                .build();
    }

    @Override
    public QuizResponse getAllQuizzesByAuthorIdAndQuizId(Long authorId, Long quizId) {
        return quizRepository.findByAuthorIdAndId(authorId, quizId).toResponse();
    }

    public void verifyQuestionRequest(QuestionRequest request) {
        if (request.getQuestionType() == QuestionType.TRUE_FALSE && request.getAnswers() != null && !request.getAnswers().isEmpty()) {
            throw new BadRequestException("True and False question doesn't need to submit answer");
        }

        if (request.getQuestionType() == QuestionType.TRUE_FALSE && request.getTrueFalseAnswer() == null) {
            throw new BadRequestException("True and False question need to specify the true false answer");
        }

        if (request.getQuestionType() != QuestionType.TRUE_FALSE && request.getAnswers().size() < 2) {
            throw new BadRequestException("Mutiple choice question need to have at least 2 answers");
        }

        if (request.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            boolean notValidAnswers = true;
            for (AnswerRequest answer : request.getAnswers()) {
                if (!notValidAnswers && answer.getIsCorrect()) {
                    throw new BadRequestException("Multiple Choice question can't have more than one correct answer");
                }
                if (notValidAnswers && answer.getIsCorrect()) {
                    notValidAnswers = false;
                }
            }

            if (notValidAnswers) {
                throw new BadRequestException("Mulitple Choice question need to have one correct answer");
            }
        }
    }

    @Override
    public QuestionResponse createQuestion(Long quizId, QuestionRequest request) {
        Quiz foundQuiz = findQuizById(quizId);
        if (!getCurrentUser().getId().equals(foundQuiz.getAuthor().getId())) {
            throw new BadRequestException("Only the author can create question for their quiz");
        }

        verifyQuestionRequest(request);
        Question question = request.toEntity(foundQuiz);
        if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE && request.getAnswers() != null) {
            question.setAnswers(request.getAnswers().stream().map(a -> a.toEntity(question)).toList());
        }
        if (question.getQuestionType() == QuestionType.TRUE_FALSE && question.getTrueFalseAnswer() != null) {
            List<Answer> answers = new ArrayList<>();
            answers.add(Answer.builder()
                    .content("True")
                    .isCorrect(question.getTrueFalseAnswer())
                    .question(question)
                    .build());
            answers.add(Answer.builder()
                    .content("False")
                    .isCorrect(!question.getTrueFalseAnswer())
                    .question(question)
                    .build());
            question.setAnswers(answers);
        }
        return questionRepository.save(question).toResponse();
    }

    @Override
    public AnswerResponse createAnswer(Long questionId, AnswerRequest request) {
        Question foundQuestion = findQuestionById(questionId);
        if (!getCurrentUser().getId().equals(foundQuestion.getQuiz().getAuthor().getId())) {
            throw new BadRequestException("Only the author can add answer for their question");
        }

        Answer answer = request.toEntity(foundQuestion);
        return answerRepository.save(answer).toResponse();
    }

    @Override
    public AnswerStudentResponse chooseAnswer(Long takeQuizId, Long answerId) {
        TakeQuiz foundTakeQuiz = takeQuizRepository.findById(takeQuizId)
                .orElseThrow(() -> new NotFoundException("Quiz session with ID: " + takeQuizId + " not found"));
        Answer foundAnswer = findAnswerById(answerId);

        if (foundTakeQuiz.getIsSubmitted()) {
            throw new BadRequestException("The quiz is already submitted");
        }

        UserAnswer foundUserAnswer = userAnswerRepository.findByUserAndQuestionAndTakeQuiz(getCurrentUser(),
                foundAnswer.getQuestion(), foundTakeQuiz);

        if (foundUserAnswer != null) {
            userAnswerRepository.deleteByUserAndQuestionAndTakeQuiz(getCurrentUser(), foundAnswer.getQuestion(),
                    foundTakeQuiz);
            userAnswerRepository.flush();
        }

        UserAnswer newUserAnswer = UserAnswer.builder()
                .user(getCurrentUser())
                .question(foundAnswer.getQuestion())
                .takeQuiz(foundTakeQuiz)
                .answer(foundAnswer)
                .isCorrect(foundAnswer.getIsCorrect())
                .build();

        UserAnswer savedUserAnswer = userAnswerRepository.save(newUserAnswer);
        return savedUserAnswer.getAnswer().toStudentResponse();
    }

    @Override
    public TakeQuizResponse studentTakeQuiz(Long quizId) {
        Quiz foundQuiz = findQuizById(quizId);
        List<TakeQuiz> takeQuizzes = takeQuizRepository.findByQuizAndUserAndIsSubmitted(foundQuiz, getCurrentUser(),
                true);
        List<TakeQuiz> unSubmittedQuizzes = takeQuizRepository.findByQuizAndUserAndIsSubmitted(foundQuiz,
                getCurrentUser(), false);

        if (takeQuizzes.size() >= foundQuiz.getMaxAttempts()) {
            throw new BadRequestException("You already exceeded the attempts for this quiz");
        }

        if (!unSubmittedQuizzes.isEmpty()) {
            // Force submit all unsubmitted quiz sessions
            unSubmittedQuizzes.forEach(takeQuiz -> {
                takeQuiz.setIsSubmitted(true);
                // Optionally set score to 0 or calculate based on current answers
                if (takeQuiz.getScore() == null) {
                    takeQuiz.setScore(0);
                }
            });
            takeQuizRepository.saveAll(unSubmittedQuizzes);
        }

        TakeQuiz newTakeQuiz = TakeQuiz.builder()
                .user(getCurrentUser())
                .quiz(foundQuiz)
                .deadlineTime(LocalDateTime.now().plusMinutes(foundQuiz.getDurationMinutes()))
                .build();

        TakeQuiz savedTakeQuiz = takeQuizRepository.save(newTakeQuiz);
        takeQuizRepository.flush();
        scheduleForceSubmitJob(savedTakeQuiz);
        return savedTakeQuiz.toResponse();
    }

    @Override
    public void submitTakenQuiz(Long takeQuizId, List<Long> answerIds) {
        TakeQuiz foundTakeQuiz = takeQuizRepository.findById(takeQuizId)
                .orElseThrow(() -> new NotFoundException("Quiz session with ID: " + takeQuizId + " not found"));

        if (foundTakeQuiz.getIsSubmitted()) {
            throw new BadRequestException("Quiz session is already submitted");
        }

        List<Answer> foundAnswers = answerIds.stream().map(this::findAnswerById).toList();

        List<UserAnswer> foundUserAnswers = userAnswerRepository.findByUserAndTakeQuiz(getCurrentUser(), foundTakeQuiz);

        if (!foundUserAnswers.isEmpty()) {
            userAnswerRepository.deleteByUserAndTakeQuiz(getCurrentUser(), foundTakeQuiz);
            userAnswerRepository.flush();
        }

        List<UserAnswer> newUserAnswers = foundAnswers.stream().map(a -> UserAnswer.builder()
                .user(getCurrentUser())
                .question(a.getQuestion())
                .takeQuiz(foundTakeQuiz)
                .answer(a)
                .isCorrect(a.getIsCorrect())
                .build()).toList();

        List<UserAnswer> correctUserAnswer = newUserAnswers.stream().filter(UserAnswer::getIsCorrect)
                .toList();

        Integer takeQuizScore = 0;
        for (UserAnswer userAnswer : correctUserAnswer) {
            takeQuizScore += userAnswer.getQuestion().getScore();
        }

        userAnswerRepository.saveAll(newUserAnswers);
        foundTakeQuiz.setIsSubmitted(true);
        foundTakeQuiz.setScore(takeQuizScore);
        TakeQuiz savedTakeQuiz = takeQuizRepository.save(foundTakeQuiz);

        userAnswerRepository.flush();
        takeQuizRepository.flush();
        cancelForceSubmitJob(savedTakeQuiz);

        try {
            UserLearningStreak foundUserLearningStreak = userLearningStreakRespository.findByAppUser(getCurrentUser());
            if (foundUserLearningStreak == null) {
                userLearningStreakRespository.save(UserLearningStreak.builder().appUser(getCurrentUser()).build());
            } else {
                if (!foundUserLearningStreak.getLastDayLearning().toLocalDate().equals(LocalDate.now())) {
                    foundUserLearningStreak.setLearningStreakDay(foundUserLearningStreak.getLearningStreakDay() + 1);
                    foundUserLearningStreak.setLastDayLearning(LocalDateTime.now());
                    userLearningStreakRespository.save(foundUserLearningStreak);
                }
            }

            leaderboardService.updateLeaderboardQuizPoint();
        } catch (Exception e) {
            log.error("Exception occurred while submitting TakeQuiz", e);
        }
    }

    @Override
    public TakeQuizResponse getTakenQuizById(Long takeQuizId) {
        TakeQuiz foundTakeQuiz = takeQuizRepository.findById(takeQuizId)
                .orElseThrow(() -> new NotFoundException("Quiz session with ID: " + takeQuizId + " not found"));

        return foundTakeQuiz.toResponse();
    }

    @Override
    public void studentDeleteTakeQuiz(Long takeQuizId) {
        TakeQuiz foundTakeQuiz = takeQuizRepository.findById(takeQuizId)
                .orElseThrow(() -> new NotFoundException("Quiz session with ID: " + takeQuizId + " not found"));

        if (!foundTakeQuiz.getUser().getId().equals(getCurrentUser().getId())) {
            throw new BadRequestException("You are not the student of the quiz session");
        }
        if (foundTakeQuiz.getIsSubmitted()) {
            throw new BadRequestException("Quiz session is already submitted");
        }

        takeQuizRepository.delete(foundTakeQuiz);
        cancelForceSubmitJob(foundTakeQuiz);
    }

    @Override
    public List<QuizResultResponse> getQuizResult(Long quizId) {
        Quiz foundQuiz = findQuizById(quizId);
        List<TakeQuiz> foundTakeQuizzes = takeQuizRepository.findByQuizAndUserAndIsSubmitted(foundQuiz, getCurrentUser(), true).stream().sorted(Comparator.comparing(TakeQuiz::getCreatedAt)).toList();

        List<Integer> indexes = IntStream.range(0, foundTakeQuizzes.size()).boxed().toList();
        return indexes.stream().map((i) -> foundTakeQuizzes.get(i).toQuizResultResponse(i + 1)).toList();
    }
}
