<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html lang="en">
	<head>
	    <base href="<%=basePath%>resources/demo/marketo/">
	    <meta charset="utf-8">
	    <title>Subscription Signup | Marketo</title>
	    <link rel="stylesheet" href="stylesheet.css">
	    <!-- 依赖库 -->
	    <script type="text/javascript" src="<%=basePath%>va/lib/jquery.js"></script>
	    <script type="text/javascript" src="<%=basePath%>va/lib/jquery.form.js"></script>
	    <!-- 核心库 -->
	    <script type="text/javascript" src="<%=basePath%>va/validator.js"></script>
	    <!-- 验证器 -->
        <script type="text/javascript" src="<%=basePath%>va/validation-js/demo_marketo.js"></script>
        <script type="text/javascript" src="jquery.maskedinput.js"></script>
        <script type="text/javascript" src="mktSignup.js"></script>
	    <script type="text/javascript">
	         function error(error, element) {
	             // 除‘co_url’以外的都不显示错误描述
	             if (element.name != "co_url") {
	                 error.html("");
	             }
	         }
	         function invalidHandler(e, validator) {
	             var errors = validator.numberOfInvalids();
	             if (errors) {
	                 var message = errors == 1
	                     ? 'You missed 1 field. It has been highlighted below'
	                     : 'You missed ' + errors + ' fields.  They have been highlighted below';
	                 $("div.error span").html(message);
	                 $("div.error").show();
	             } else {
	                 $("div.error").hide();
	             }
	         }
	         $(function() {
	             $("#profileForm").validate({
	                 ajaxsubmit: {
	                    success: function (res) {
	                       if (res == "success") {
	                           location.href = "<%=basePath%>demo/marketo/step2";
	                       } else {
	                           console.error(res);
	                       }
	                    }
	                 }
	             });
	         });
	    </script>
	</head>
	<body>
		<!-- start page wrapper -->
		<div id="letterbox">
		    <!-- start header container -->
		    <div id="header-background">
		        <div class="nav-global-container">
		            <div class="login">
		                <a href="#"><span></span>Customer Login</a>
		            </div>
		            <div class="logo">
		                <a href="#"><img src="images/logo_marketo.gif" width="168" height="73" alt="Marketo"></a>
		            </div>
		            <div class="nav-global">
		                <ul>
		                    <li><a href="#" class="nav-g01"><span></span>Home</a></li>
		                    <li><a href="#" class="nav-g02"><span></span>Products</a></li>
		                    <li><a href="#" class="nav-g04"><span></span>B2B Marketing Resources</a></li>
		                    <li><a href="#" class="nav-g05"><span></span>About Marketo</a></li>
		                </ul>
		            </div>
		        </div>
		    </div>
		    <!-- end header container -->
		    <div class="line-grey-tier"></div>
		    <!-- start page container 2 div-->
		    <div id="page-container" class="resize">
		        <div id="page-content-inner" class="resize">
		            <!-- start col-main -->
		            <div id="col-main" class="resize">
		                <!-- start main content -->
		                <div class="main-content resize">
		                    <div class="action-container" style="display:none;"></div>
		                    <h1>Step 1 of 2</h1>
		                    <p></p>
		                    <br clear="all">
		                    <div>
		                        <form id="profileForm" type="actionForm" action="<%=basePath%>demo/marketo/do/default" method="post" valid="{error: error, invalidHandler: invalidHandler}">
		                            <div class="error" style="display:none;">
		                                <img src="images/warning.gif" alt="Warning!" width="24" height="24" style="float:left; margin: -5px 10px 0px 0px;">
		                                <span></span>.
		                                <br clear="all">
		                            </div>
		                            <table cellpadding="0" cellspacing="0" border="0">
		                                <tr>
		                                    <td class="label">
		                                        <label for="co_name">Company Name:</label>
		                                    </td>
		                                    <td class="field">
		                                        <input id="co_name" name="co_name" size="20" type="text" tabindex="1" value="">
		                                    </td>
		                                </tr>
		                                <tr>
		                                    <td class="label">
		                                        <label for="co_url">Company URL:</label>
		                                    </td>
		                                    <td class="field">
		                                        <input id="co_url" name="co_url" style="width:163px" type="text" tabindex="2" value="http://">
		                                    </td>
		                                </tr>
		                                <tr>
		                                    <td>
		                                    <td>
		                                </tr>
		                                <tr>
		                                    <td class="label">
		                                        <label for="first_name">First Name:</label>
		                                    </td>
		                                    <td class="field">
		                                        <input id="first_name" name="first_name" size="20" type="text" tabindex="3" value="">
		                                    </td>
		                                </tr>
		                                <tr>
		                                    <td class="label">
		                                        <label for="last_name">Last Name:</label>
		                                    </td>
		                                    <td class="field">
		                                        <input id="last_name" name="last_name" size="20" type="text" tabindex="4" value="">
		                                    </td>
		                                </tr>
		                                <tr>
		                                    <td class="label">
		                                        <label for="address1">Company Address:</label>
		                                    </td>
		                                    <td class="field">
		                                        <input maxlength="40" name="address1" size="20" type="text" tabindex="5" value="">
		                                    </td>
		                                </tr>
		                                <tr>
		                                    <td class="label"></td>
		                                    <td class="field">
		                                        <input maxlength="40" name="address2" size="20" type="text" tabindex="6" value="">
		                                    </td>
		                                </tr>
		                                <tr>
		                                    <td class="label">
		                                        <label for="city">City:</label>
		                                    </td>
		                                    <td class="field">
		                                        <input maxlength="40" name="city" size="20" type="text" tabindex="7" value="">
		                                    </td>
		                                </tr>
		                                <tr>
		                                    <td class="label">
		                                        <label for="state">State:</label>
		                                    </td>
		                                    <td class="field">
		                                        <select id="state" name="state" style="margin-left: 4px;" tabindex="8">
		                                            <option value="">Choose State:</option>
		                                            <option value="AL">Alabama</option>
		                                            <option value="AK">Alaska</option>
		                                            <option value="AZ">Arizona</option>
		                                            <option value="AR">Arkansas</option>
		                                            <option value="CA">California</option>
		                                            <option value="CO">Colorado</option>
		                                            <option value="CT">Connecticut</option>
		                                            <option value="DE">Delaware</option>
		                                            <option value="FL">Florida</option>
		                                            <option value="GA">Georgia</option>
		                                            <option value="HI">Hawaii</option>
		                                            <option value="ID">Idaho</option>
		                                            <option value="IL">Illinois</option>
		                                            <option value="IN">Indiana</option>
		                                            <option value="IA">Iowa</option>
		                                            <option value="KS">Kansas</option>
		                                            <option value="KY">Kentucky</option>
		                                            <option value="LA">Louisiana</option>
		                                            <option value="ME">Maine</option>
		                                            <option value="MD">Maryland</option>
		                                            <option value="MA">Massachusetts</option>
		                                            <option value="MI">Michigan</option>
		                                            <option value="MN">Minnesota</option>
		                                            <option value="MS">Mississippi</option>
		                                            <option value="MO">Missouri</option>
		                                            <option value="MT">Montana</option>
		                                            <option value="NE">Nebraska</option>
		                                            <option value="NV">Nevada</option>
		                                            <option value="NH">New Hampshire</option>
		                                            <option value="NJ">New Jersey</option>
		                                            <option value="NM">New Mexico</option>
		                                            <option value="NY">New York</option>
		                                            <option value="NC">North Carolina</option>
		                                            <option value="ND">North Dakota</option>
		                                            <option value="OH">Ohio</option>
		                                            <option value="OK">Oklahoma</option>
		                                            <option value="OR">Oregon</option>
		                                            <option value="PA">Pennsylvania</option>
		                                            <option value="RI">Rhode Island</option>
		                                            <option value="SC">South Carolina</option>
		                                            <option value="SD">South Dakota</option>
		                                            <option value="TN">Tennessee</option>
		                                            <option value="TX">Texas</option>
		                                            <option value="UT">Utah</option>
		                                            <option value="VT">Vermont</option>
		                                            <option value="VA">Virginia</option>
		                                            <option value="WA">Washington</option>
		                                            <option value="WV">West Virginia</option>
		                                            <option value="WI">Wisconsin</option>
		                                            <option value="WY">Wyoming</option>
		                                        </select>
		                                    </td>
		                                </tr>
		                                <tr>
		                                    <td class="label">
		                                        <label for="zip">Zip:</label>
		                                    </td>
		                                    <td class="field">
		                                        <input name="zip" style="width: 100px" type="text" tabindex="9" value="">
		                                    </td>
		                                </tr>
		                                <tr>
		                                    <td class="label">
		                                        <label for="phone">Phone:</label>
		                                    </td>
		                                    <td class="field">
		                                        <input id="phone" name="phone" type="text" tabindex="10" value="">
		                                    </td>
		                                </tr>
		                                <tr>
		                                    <td colspan="2">
		                                        <h2 style="border-bottom: 1px solid #CCCCCC;">Login Information</h2>
		                                    </td>
		                                </tr>
		                                <tr>
		                                    <td class="label">
		                                        <label for="email">Email:</label>
		                                    </td>
		                                    <td class="field">
		                                        <input id="email" name="email" size="20" type="text" tabindex="11" value="">
		                                    </td>
		                                </tr>
		                                <tr>
		                                    <td class="label">
		                                        <label for="password1">Password:</label>
		                                    </td>
		                                    <td class="field">
		                                        <input id="password1" name="password1" size="20" type="password" tabindex="12" value="">
		                                    </td>
		                                </tr>
		                                <tr>
		                                    <td class="label">
		                                        <label for="password2">Retype Password:</label>
		                                    </td>
		                                    <td class="field">
		                                        <input id="password2" name="password2" size="20" type="password" tabindex="13" value="">
		                                        <div class="formError"></div>
		                                    </td>
		                                </tr>
		                                <tr>
		                                    <td></td>
		                                    <td>
		                                        <div class="buttonSubmit">
		                                            <span></span>
		                                            <input class="formButton" type="submit" value="Next" style="width: 140px" tabindex="14">
		                                        </div>
		                                    </td>
		                                </tr>
		                            </table>
		                            <br>
		                            <br>
		                        </form>
		                        <br clear="all">
		                    </div>
		                </div>
		                <!-- end main content -->
		                <br>
		            </div>
		            <!-- end col-main -->
		            <!-- start left col -->
		            <div id="col-left" class="nav-left-back empty resize" style="position: absolute; min-height: 450px;">
		                <div class="col-left-header-tab" style="position: absolute;">Signup</div>
		                <div class="nav-left"></div>
		                <div class="left-nav-callout png" style="top: 15px; margin-bottom: 100px;">
		                    <img src="images/left-nav-callout-long.png" class="png" alt="">
		                    <h6>Sign Up Process</h6>
		                    <a style="background-image: url(images/step1-24.gif); font-weight: normal; text-decoration: none; cursor: default;">Sign up with a valid credit card.</a>
		                    <a style="background-image: url(images/step2-24.gif); font-weight: normal; text-decoration: none; cursor: default;">Connect to your Google AdWords account.  You will need your AdWords Customer ID.</a>
		                    <a style="background-image: url(images/step3-24.gif); font-weight: normal; text-decoration: none; cursor: default;">Start your 30 day trial.  No payments until trial ends.</a>
		                </div>
		                <div class="footerAddress">
		                    <b>Marketo Inc.</b>
		                    <br>1710 S. Amphlett Blvd.
		                    <br>San Mateo, CA 94402 USA
		                    <br>
		                </div>
		                <br clear="all">
		            </div>
		            <!-- end left col -->
		        </div>
		    </div>
		    <!-- end page container 2 divs-->
		    <div id="footer-container" align="center">
		        <div class="footer">
		            <ul>
		                <li><a href="..">Home</a>
		                </li>
		                <li class="line-off"><a href="step2.htm">Second step</a>
		                </li>
		            </ul>
		        </div>
		    </div>
		    <!-- end page wrapper -->
		</div>
	</body>
</html>
