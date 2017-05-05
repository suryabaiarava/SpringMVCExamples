package com.crunchify.controller;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookLink;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@RequestMapping(value = "/social/facebook")
@Controller
public class FacebookController {

	private static final String FACEBOOK = "facebook";
	private static Log log = LogFactory.getLog(TwitterController.class);

	@Autowired
	private ConnectionFactoryRegistry facebookconnectionFactoryRegistry;

	@Autowired
	private OAuth2Parameters oAuth2Parameters;

	@Autowired
	@Qualifier("facebookServiceProvider")
	private OAuthServiceProvider<FacebookApi> facebookServiceProvider;

	@Value("${app.facebook.pageId}")
	private String pageId;

	Token accessToken;

	@RequestMapping("/sigin")
	public ModelAndView facebookSigIn(HttpServletRequest request,
			HttpServletResponse response) {
		log.info("Establish connection with facebook");
		FacebookConnectionFactory facebookConnectionFactory = (FacebookConnectionFactory) facebookconnectionFactoryRegistry
				.getConnectionFactory(FACEBOOK);

		OAuth2Operations oauthOperations = facebookConnectionFactory
				.getOAuthOperations();

		String authorizeUrl = oauthOperations.buildAuthorizeUrl(
				GrantType.AUTHORIZATION_CODE, oAuth2Parameters);

		RedirectView redirectView = new RedirectView(authorizeUrl, true, true,
				true);
		
		return new ModelAndView(redirectView);
	}

	@RequestMapping(value = "/callback")
	
	public String facebookCallBack(@RequestParam("code") String code,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		log.info("Getting code from facebook");
		try {
			OAuthService oAuthService = facebookServiceProvider.getService();
			System.out.println("code--" + code);
			Verifier verifier = new Verifier(code);

			accessToken = oAuthService.getAccessToken(Token.empty(), verifier);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "facebookData";

	}

	@RequestMapping(value = "/postData")
	
	public String facebookPostData(
			@RequestParam("contentvalue") String postData,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		log.info("Getting code from facebook");
		try {
			Facebook facebook = new FacebookTemplate(accessToken.getToken(),
					"myapp");

			facebook.pageOperations().post(pageId, postData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "facebookData";
	}
}