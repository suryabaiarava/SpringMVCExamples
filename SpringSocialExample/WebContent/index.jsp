<!DOCTYPE html>
<html data-ng-app="enterprise">
<head>




<script>

function shareOnLinkedin()
{
	
	window.location.replace("/SampleServlet/social/linkedin/signin");
	
	}

function shareOnFacebook()
{
	
	window.location.replace("/SampleServlet/social/facebook/sigin");
	
	}
function shareOnTwitter()
{
	
	window.location.replace("/SampleServlet/social/twitter/signin");
	
	}

</script>
</head>
<body>
	<h2>Spring Social</h2>
	<div data-ng-controller="SocialController">
	
	
	
	
	
	
		<button onclick='shareOnFacebook()'>Post on Facebook</button>
		<button onclick='shareOnTwitter()'>Tweet on Twitter</button>
		<button onclick='shareOnLinkedin()'>post to linkedin</button>
		
	</div>
</body>
</html>




