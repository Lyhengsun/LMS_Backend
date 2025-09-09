package com.norton.lms_backend.service.impl;

import com.norton.lms_backend.exception.BadRequestException;
import com.norton.lms_backend.exception.NotFoundException;
import com.norton.lms_backend.model.dto.request.AnswerRequest;
import com.norton.lms_backend.model.dto.request.QuestionRequest;
import com.norton.lms_backend.model.dto.request.QuizRequest;
import com.norton.lms_backend.model.dto.response.AnswerResponse;
import com.norton.lms_backend.model.dto.response.AnswerStudentResponse;
import com.norton.lms_backend.model.dto.response.PagedResponse;
import com.norton.lms_backend.model.dto.response.PaginationInfo;
import com.norton.lms_backend.model.dto.response.QuestionResponse;
import com.norton.lms_backend.model.dto.response.QuizResponse;
import com.norton.lms_backend.model.dto.response.QuizStudentResponse;
import com.norton.lms_backend.model.dto.response.TakeQuizResponse;
import com.norton.lms_backend.model.entity.Answer;
import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.Category;
import com.norton.lms_backend.model.entity.Question;
import com.norton.lms_backend.model.entity.Quiz;
import com.norton.lms_backend.model.entity.TakeQuiz;
import com.norton.lms_backend.model.entity.UserAnswer;
import com.norton.lms_backend.model.enumeration.QuestionType;
import com.norton.lms_backend.repository.AnswerRepository;
import com.norton.lms_backend.repository.QuestionRepository;
import com.norton.lms_backend.repository.QuizRepository;
import com.norton.lms_backend.repository.TakeQuizRepository;
import com.norton.lms_backend.repository.UserAnswerRepository;
import com.norton.lms_backend.service.CategoryService;
import com.norton.lms_backend.service.QuizService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;
    private final CategoryService categoryService;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final TakeQuizRepository takeQuizRepository;

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
        if (quizRequest.getQuestions().size() <= 0) {
            throw new BadRequestException("A Quiz need to have at least one question");
        }

        Quiz quiz = quizRequest.toEntity();
        quiz.setAuthor(getCurrentUser());
        Category category = categoryService.getCategory(quizRequest.getCategoryId());
        quiz.setCategory(category);

        List<Question> questions = quizRequest.getQuestions().stream().map(q -> {
            verifyQuestionRequest(q);
            Question question = q.toEntity(quiz);
            question.setAnswers(q.getAnswers().stream().map(a -> a.toEntity(question)).toList());
            return question;
        }).toList();

        quiz.setQuestions(questions);

        return quizRepository.save(quiz).toResponse();
    }

    @Override
    public PagedResponse<QuizResponse> getAllQuizzes(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Quiz> result = quizRepository.findAll(pageable);
        return PagedResponse.<QuizResponse>builder()
                .items(result.getContent().stream().map(Quiz::toResponse).toList())
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
            question.setAnswers(q.getAnswers().stream().map(a -> a.toEntity(question)).toList());
            return question;
        }).toList();

        // Update fields manually
        quiz.setQuizName(quizRequest.getQuizName());
        quiz.setQuizDescription(quizRequest.getQuizDescription());
        quiz.setQuizInstruction(quizRequest.getQuizInstruction());
        quiz.setLevel(quizRequest.getLevel());
        quiz.setDurationMinutes(quizRequest.getDurationMinutes());
        quiz.setMaxAttempts(quizRequest.getMaxAttempts());
        quiz.setPassingScore(quizRequest.getPassingScore());

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

    @Override
    public void deleteQuiz(Long id) {
        Quiz quiz = findQuizById(id);
        if (!getCurrentUser().getId().equals(quiz.getAuthor().getId()))
            throw new BadRequestException("Only the author can delete his/her own quiz");
        quizRepository.delete(quiz);
    }

    @Override
    public PagedResponse<QuizResponse> getAllYourQuizzes(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Quiz> result = quizRepository.findAllByAuthorId(getCurrentUser().getId(), pageable);
        return PagedResponse.<QuizResponse>builder()
                .items(result.getContent().stream().map(Quiz::toResponse).toList())
                .pagination(new PaginationInfo(result))
                .build();
    }

    @Override
    public PagedResponse<QuizResponse> getAllQuizzesByAuthorId(Long id, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Quiz> result = quizRepository.findAllByAuthorId(id, pageable);
        return PagedResponse.<QuizResponse>builder()
                .items(result.getContent().stream().map(Quiz::toResponse).toList())
                .pagination(new PaginationInfo(result))
                .build();
    }

    @Override
    public QuizResponse getAllQuizzesByAuthorIdAndQuizId(Long authorId, Long quizId) {
        return quizRepository.findByAuthorIdAndId(authorId, quizId).toResponse();
    }

    public void verifyQuestionRequest(QuestionRequest request) {
        if (request.getQuestionType() != QuestionType.TRUE_FALSE && request.getAnswers().size() < 2) {
            throw new BadRequestException("Mutiple choice question need to have at least 2 answers");
        }

        if (request.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            Boolean notValidAnswers = true;
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
        question.setAnswers(request.getAnswers().stream().map(a -> a.toEntity(question)).toList());
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
        TakeQuiz foundTakeQuiz = takeQuizRepository.findById(takeQuizId).orElseThrow(() -> new NotFoundException("Quiz session with ID: " + takeQuizId + " not found"));
        Answer foundAnswer = findAnswerById(answerId);

        if (foundTakeQuiz.getIsSubmitted()) {
            throw new BadRequestException("The quiz is already submitted");
        }

        UserAnswer foundUserAnswer = userAnswerRepository.findByUserAndQuestionAndTakeQuiz(getCurrentUser(), foundAnswer.getQuestion(), foundTakeQuiz);

        if (foundUserAnswer != null) {
            userAnswerRepository.deleteByUserAndQuestionAndTakeQuiz(getCurrentUser(), foundAnswer.getQuestion(), foundTakeQuiz);
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
        List<TakeQuiz> takeQuizzes = takeQuizRepository.findByQuizAndUserAndIsSubmitted(foundQuiz, getCurrentUser(), true);
        List<TakeQuiz> unSubmittedQuizzes = takeQuizRepository.findByQuizAndUserAndIsSubmitted(foundQuiz, getCurrentUser(), false);

        if (takeQuizzes.size() >= foundQuiz.getMaxAttempts()) {
            throw new BadRequestException("You already exceeded the attempts for this quiz");
        }

        if (unSubmittedQuizzes.size() > 0) {
            takeQuizRepository.deleteAll(unSubmittedQuizzes);
        }

        TakeQuiz newTakeQuiz = TakeQuiz.builder()
                .user(getCurrentUser())
                .quiz(foundQuiz)
                .deadlineTime(LocalDateTime.now().plus(foundQuiz.getDurationMinutes(), ChronoUnit.MINUTES))
                .build();

        return takeQuizRepository.save(newTakeQuiz).toResponse();
    }

    @Override
    public void submitTakenQuiz(Long takeQuizId) {
        TakeQuiz foundTakeQuiz = takeQuizRepository.findById(takeQuizId).orElseThrow(() -> new NotFoundException("Quiz session with ID: " + takeQuizId + " not found"));

        if (foundTakeQuiz.getIsSubmitted()) {
            throw new BadRequestException("Quiz session is already submitted");
        }

        foundTakeQuiz.setIsSubmitted(true);
        takeQuizRepository.save(foundTakeQuiz);
    }
}
