<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html lang="zh-CN" class="">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
        <meta http-equiv="X-ZA-Response-Id" content="17c3f98b875a4d77">
        <meta http-equiv="X-ZA-Experiment" content="default:None,nweb_sticky_sidebar:sticky,ge2:ge2_1,new_more:new,live_store:ls_a1_b1_c2_f1,topnavbar_qrcode:topnavbar_qrcode_hide,wechat_share_modal:wechat_share_modal_show,home_ui2:default,ge120:ge120_2,fav_act:default,home_nweb:default,ge3:ge3_10,iOS_newest_version:3.54.0,qrcode_login:pwd,android_inline_video_play:true,zcm-lighting:zcm,recommend_readings_on_share:wx_share_editor_recommend,qa_sticky_sidebar:sticky_sidebar">
        <meta name="renderer" content="webkit"/>
        <meta name="description" content="一个真实的网络问答社区，帮助你寻找答案，分享知识。"/>
        <meta name="viewport" content="user-scalable=no, width=device-width, initial-scale=1.0, maximum-scale=1.0"/>
        <title>知乎 - 与世界分享你的知识、经验和见解</title>
        <link rel="stylesheet" href="/resources/zhihu/main.8859d147.css">
        <!-- 依赖库 -->
	    <script type="text/javascript" src="/va/lib/jquery.js"></script>
	    <script type="text/javascript" src="/va/lib/jquery.form.js"></script>
	    <!-- 核心库 -->
	    <script type="text/javascript" src="/va/validator.js"></script>
	    <!-- 验证器 -->
	    <script type="text/javascript" src="/va/validation-js/zhihu_login.js"></script>
        <script type="text/javascript" src="/va/validation-js/zhihu_register.js"></script>
    </head>
    <body class="zhi ">
        <div class="index-main">
            <div class="index-main-body">

                <div class="index-header">
                    <h1 class="logo hide-text">知乎</h1>
                    <h2 class="subtitle">与世界分享你的知识、经验和见解</h2>
                </div>

                <div class="desk-front sign-flow clearfix sign-flow-simple">
                     
                    <div class="index-tab-navs" data-active-index="1">
                        <div class="navs-slider">
                            <a href="#signup" class="active">注册</a>
                            <a href="#signin">登录</a>
                            <span class="navs-slider-bar"></span>
                        </div>
                    </div>
                    
                    <div id="SignInForm" class="view view-signin" data-za-module="SignInForm">
                        <form valid="{use: 'zhihu_login'}" action="/zhihu/login" method="POST">
                            <div class="group-inputs">
                                <div class="account input-wrapper">
                                    <input type="text" name="account" aria-label="手机号或邮箱" placeholder="手机号或邮箱" required>
                                </div>
                                <div class="verification input-wrapper">
                                    <input type="password" name="password" aria-label="密码" placeholder="密码" required/>
                                </div>
                            </div>
                            <div class="button-wrapper command">
                                <button class="sign-button submit" type="submit">登录</button>
                            </div>
                        </form>
                    </div>

                    <div id="SignUpForm" class="view view-signup selected" data-za-module="SignUpForm">
                        <form valid="{use: 'zhihu_register'}" class="zu-side-login-box" action="/zhihu/register" id="sign-form-1" autocomplete="off" method="POST">
                            <div class="group-inputs">
                                <div class="name input-wrapper">
                                    <input required type="text" name="fullname" aria-label="姓名" placeholder="姓名">
                                </div>
                                <div class="email input-wrapper">
                                    <input required type="text" class="account" name="phone_num" aria-label="手机号" placeholder="手机号">
                                </div>
                                <div class="input-wrapper">
                                    <input required type="password" name="password" aria-label="密码" placeholder="密码（不少于 6 位）" autocomplete="off">
                                </div>
                            </div>
                            <div class="button-wrapper command">
                                <button class="sign-button submit" type="submit">注册知乎</button>
                            </div>
                        </form>
                    </div>

                </div>
            </div>
        </div>
        <script type="text/javascript" src="/resources/zhihu/main.js"></script>
        <script type="text/javascript">
            // 设置默认配置（如果多个表单相关处理不同请单独设置）
            $.extend($.validator.defaults, {
                // 启用ajaxsubmit（默认为启用，相关参数请参考：http://malsup.com/jquery/form/）
                ajaxsubmit: {
                    // 提交成功，并验证通过后的回调事件
                    success: function (res) {
                        alert(res);
                    }
                },
                // 元素验证通过事件
                success: function(error, element) {
                    // 验证通过的时候删除样式
                    error.removeClass("is-visible");
                },
                // 元素验证失败事件
                error: function(error, element) {
                    // 验证失败的时候添加样式
                    setTimeout(function () {
                        error.addClass("is-visible");
                    }, 100);
                },
                // 布置错误标签
                errorPlacement: function (error, element) {
                    // 将错误标签加到表单元素后面
                    error.insertAfter(element);
                    // 点击错误标签时异常错误标签
                    var validator = $.data(element.parents('form')[0], "validator");
                    $(error).bind("click", function() {
                        error.removeClass("is-visible");
                        setTimeout(function () {
                            validator.hideThese(error);
                        }, 200);
                    });
                },
                // 聚焦事件
                onfocusin: function(element) {
                    this.lastActive = element;
                    // 聚焦时候隐藏错误提醒
                    var error = this.errorsFor(element),
                        self = this;
                    error.removeClass("is-visible");
                    setTimeout(function () {
                        self.hideThese(error);
                    }, 200);
                },
                // 关闭自动聚焦错误元素
                focusInvalid: false
            });
        </script>
    </body>
</html>
