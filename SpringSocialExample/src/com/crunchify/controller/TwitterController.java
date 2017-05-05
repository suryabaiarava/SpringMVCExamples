package com.crunchify.controller;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter.api.TimelineOperations;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@RequestMapping(value = "/social/twitter")
@Controller
public class TwitterController {

	private static Log log = LogFactory.getLog(TwitterController.class);

	@Autowired
	private ConnectionFactoryRegistry connectionFactoryRegistry;

	@Autowired
	private OAuth1Parameters oAuth1Parameters;

	@Value("${app.config.oauth.twitter.callback}")
	private String callback;

	@Value("${app.config.oauth.twitter.apikey}")
	private String consumerKey;

	@Value("${app.config.oauth.twitter.apisecret}")
	private String consumerSecret;

	OAuthToken oAuthToken;
	OAuth1Operations oauthOperations;

	String value;

	String secret;

	@RequestMapping(value = "/signin", method = RequestMethod.GET)
	public ModelAndView signin(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		log.info("Establishing connection with twitter");

		TwitterConnectionFactory twitterConnectionFactory = (TwitterConnectionFactory) connectionFactoryRegistry
				.getConnectionFactory(Twitter.class);

		oauthOperations = twitterConnectionFactory.getOAuthOperations();
		oAuthToken = oauthOperations.fetchRequestToken(callback,
				oAuth1Parameters);
		String authorizeUrl = oauthOperations.buildAuthenticateUrl(
				oAuthToken.getValue(), oAuth1Parameters);
		RedirectView redirectView = new RedirectView(authorizeUrl, true, true,
				true);

		return new ModelAndView(redirectView);
	}

	@RequestMapping(value = "/twittercallback")
	public String springSocialCallback(
			@RequestParam("oauth_token") String oauthToken,
			@RequestParam("oauth_verifier") String oauthVerifier,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		log.info("Receiving tokens from twitter API");
		OAuthToken accessToken = oauthOperations.exchangeForAccessToken(
				new AuthorizedRequestToken(oAuthToken, oauthVerifier), null);

		value = accessToken.getValue();
		secret = accessToken.getSecret();

		return "twitterData";
	}

	@RequestMapping(value = "/tweetData")
	public String springSocialTwitter(
			@RequestParam("contentvalue") String tweetData,

			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Twitter twitterTemplate = new TwitterTemplate(consumerKey,
				consumerSecret, value, secret);

		TwitterProfile profile = twitterTemplate.userOperations()
				.getUserProfile();

		TimelineOperations timelineOperations = twitterTemplate
				.timelineOperations();
		log.info("Tweet Data"+tweetData);
		timelineOperations.updateStatus(tweetData);

		
		return "twitterData";
	}
}
