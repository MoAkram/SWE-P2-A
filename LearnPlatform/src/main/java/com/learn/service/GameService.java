package com.learn.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.learn.model.Achievement;
import com.learn.model.Course;
import com.learn.model.Game;
import com.learn.model.Question;
import com.learn.model.QuestionListWrapper;
import com.learn.repository.AchievementRepository;
import com.learn.repository.CourseRepository;
import com.learn.repository.GameRepository;
import com.learn.repository.QuestionRepository;

@Service
public class GameService {
	@Autowired
	GameRepository gamerepo;
	@Autowired
	CourseRepository courserepo;
	@Autowired
	QuestionRepository questionrepo;
	@Autowired
	AchievementRepository achievrepo;

	public ModelAndView intializeCoursePage(Long courseId) {
		Course currentCourse = courserepo.FindById(courseId);
		ArrayList<Game> courseGames = gamerepo.FindByCourseId(courseId);
		ModelAndView mav = new ModelAndView();
		mav.setViewName("Games");
		mav.addObject("Course", currentCourse);
		mav.addObject("GamesInCourse", courseGames);
		return mav;
	}

	public ModelAndView createGameIntiate(ModelAndView mav, Long CourseId) {
		ArrayList<Question> questions = new ArrayList<Question>(5);
		Question dummyQuestion = new Question();
		for (int i = 1; i < 6; i++) {
			questions.add(dummyQuestion);
		}
		mav.setViewName("Games");
		mav.addObject("Game", new Game());
		QuestionListWrapper wrapper = new QuestionListWrapper();
		wrapper.setQuestions(questions);
		mav.addObject("wrapper", wrapper);
		Course x = courserepo.FindById(CourseId);
		mav.addObject("Course", x);
		return mav;
	}

	public ModelAndView addGame(Long creatorId, Long courseId, Game game, QuestionListWrapper questionWrapper) {
		game.setCourseID(courseId);
		game.setTeacherID(creatorId);
		gamerepo.save(game);
		Long savedGameId = gamerepo.FindByName(game.getName()).getId();
		questionWrapper.saveGameId(savedGameId);
		questionrepo.save(questionWrapper.getQuestions());
		String coursePg = "redirect:/Course/" + courseId;
		ModelAndView mav = new ModelAndView();
		mav.setViewName(coursePg);
		return mav;
	}

	public ModelAndView showGame(Long gameId) {
		Game currentGame = gamerepo.FindById(gameId);
		QuestionListWrapper wrapper = new QuestionListWrapper();
		ArrayList<Question> gameQuestions = questionrepo.FindByGameId(gameId);
		for(int i=0;i<gameQuestions.size();i++){
			gameQuestions.get(i).setAnswer(null);
		}
		wrapper.setQuestions(gameQuestions);
		ModelAndView mav = new ModelAndView();
		mav.setViewName("PlayGame");
		mav.addObject("Game", currentGame);
		mav.addObject("wrapper", wrapper);
		return mav;
	}

	public ModelAndView evaluateGame(Long gameId, QuestionListWrapper userGame, Long userId) {
		ArrayList<Question> gameQuestions = questionrepo.FindByGameId(gameId);
		ArrayList<Question> userAnswers = userGame.getQuestions();// Equivalent
																	// to Get
																	// Answers
		int score = 0;
		for (int i = 0; i < gameQuestions.size(); i++) {
			if (gameQuestions.get(i).getAnswer() == userAnswers.get(i).getAnswer())
				score++;
		}
		Achievement newAchiev = new Achievement();
		newAchiev.setGameId(gameId);
		newAchiev.setUserId(userId);
		newAchiev.setScore(score);
		achievrepo.save(newAchiev);
		Game currentGame=gamerepo.FindById(gameId);
		ModelAndView mav = new ModelAndView();
		mav.setViewName("PlayGame");
		mav.addObject("result", score);
		mav.addObject("Game", currentGame);
		return mav;
	}
	public ModelAndView showAllGames(){
		ArrayList<Game> Games = gamerepo.FindAllGames();
		ModelAndView mav=new ModelAndView();
		mav.setViewName("AllGamesAndCourses");
		mav.addObject("Games", Games);
		return mav;
	}
	public ModelAndView showAchiev(long id){
		ArrayList<Achievement> achievs=achievrepo.FindAchiev(id);
		ArrayList<Game> games= new ArrayList<Game>();
		for(int i=0;i<achievs.size();i++){
			long gamedId= achievs.get(i).getGameId();
			Game game=gamerepo.FindById(gamedId);
			games.add(game);
		}
		ModelAndView mav =new ModelAndView();
		mav.setViewName("Achievs");
		mav.addObject("Achievs", achievs);
		mav.addObject("Games",games);
		return mav;
	}
}
