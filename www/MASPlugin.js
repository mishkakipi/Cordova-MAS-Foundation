/**
 * Copyright (c) 2016 CA, Inc. All rights reserved.
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 *
 */

var MASPluginCallbacks = require("./MASPluginCallbacks"),
    MASPluginUtils = require("./MASPluginUtils");

var MASPlugin = {
    
    /**
     MAS which has the interfaces mapped to the native MAS class.
     */
    MAS: function() {
        
        /**
         * Initializes the MAS plugin. This includes setting of the various listeners required
         * for authenticating the user while registration of the application with the Gateway
         * and accessing various protected api. Any further initialization related setting will go here.
         */
        this.initialize = function(successHandler, errorHandler) {
            
            Cordova.exec(MASPluginCallbacks.MASAuthenticationCallback, errorHandler, "MASPlugin", "setAuthenticationListener", []);
            
            Cordova.exec(MASPluginCallbacks.MASOTPChannelSelectCallback, errorHandler, "MASPlugin", "setOTPChannelSelectorListener", []);
            
            Cordova.exec(MASPluginCallbacks.MASOTPAuthenticationCallback, errorHandler, "MASPlugin", "setOTPAuthenticationListener", []);
            
            // TODO: Check for success or error
            return successHandler("Initialization success !!");
        };

        /**
         * Use Native MASUI
         */
        this.useNativeMASUI = function(successHandler, errorHandler) {
            
            Cordova.exec(successHandler, errorHandler, "MASPlugin", "useNativeMASUI", []);
        };

        /**
         * Set the authentication UI handling page by this plugin.
         *
         * @param successHandler user defined success callback
         * @param errorHandler user defined error callback
         * @param customPage user defined page if you want the plugin to use it.
         *     "mas-login.html" is the default page.
         */
        this.setCustomLoginPage = function(successHandler, errorHandler, customPage) {
            
            if (customPage) {
                
                $.ajax({
                    url: customPage,
                    success: function() {
                        MASPlugin.MASConfig.loginPage = customPage;
                        return successHandler("Login page set to :" + MASPlugin.MASConfig.loginPage);
                    },
                    error: function() {
                        MASPlugin.MASConfig.loginPage = "masui/mas-login.html";
                        return errorHandler({
                            errorMessage: "Can't find " + customPage
                        });
                    }
                });
            } 
            else 
            {
                MASPlugin.MASConfig.loginPage = "masui/mas-login.html";
                return errorHandler({
                    errorMessage: "Can't find " + customPage
                });
            }
        };

        /**
         * Set the OTP Channels Selection UI handling page by this plugin.
         *
         * @param successHandler user defined success callback
         * @param errorHandler user defined error callback
         * @param customPage user defined page if you want the plugin to use it.
         *     "mas-otpchannel.html" is the default page.
         */
        this.setCustomOTPChannelsPage = function(successHandler, errorHandler, customPage) {
            if (customPage) {
                $.ajax({
                    url: customPage,
                    success: function() {
                        MASPlugin.MASConfig.otpChannelsPage = customPage;
                        return successHandler("OTP Channels page set to :" + MASPlugin.MASConfig.otpChannelsPage);
                    },
                    error: function() {
                        MASPlugin.MASConfig.otpChannelsPage = "mas-otpchannel.html";
                        return errorHandler({
                            errorMessage: "Can't find " + customPage
                        });
                    }
                });
            } else {
                MASPlugin.MASConfig.otpChannelsPage = "mas-otpchannel.html";
                return errorHandler({
                    errorMessage: "Can't find " + customPage
                });
            }
        };

        /**
         * Set the OTP UI handling page by this plugin.
         *
         * @param successHandler user defined success callback
         * @param errorHandler user defined error callback
         * @param customPage user defined page if you want the plugin to use it.
         *     "mas-otp.html" is the default page.
         */
        this.setCustomOTPPage = function(successHandler, errorHandler, customPage) {
            if (customPage) {
                $.ajax({
                    url: customPage,
                    success: function() {
                        MASPlugin.MASConfig.otpPage = customPage;
                        return successHandler("OTP page set to :" + MASPlugin.MASConfig.otpPage);
                    },
                    error: function() {
                        MASPlugin.MASConfig.otpPage = "mas-otp.html";
                        return errorHandler({
                            errorMessage: "Can't find " + customPage
                        });
                    }
                });
            } else {
                MASPlugin.MASConfig.otpPage = "mas-otp.html";
                return errorHandler({
                    errorMessage: "Can't find " + customPage
                });
            }
        };

        /**
         Sets the device registration type MASDeviceRegistrationType. This should be set before MAS start is executed.
         */
        this.grantFlow = function(successHandler, errorHandler, MASGrantFlow) {
        
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "setGrantFlow", [MASGrantFlow]);
        };

        /**
         Set the name of the configuration file.  This gives the ability to set the file's name to a custom value.
         */
        this.configFileName = function(successHandler, errorHandler, fileName) {
         
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "setConfigFileName", [fileName]);
        };

        /**
         Starts the lifecycle of the MAS processes. This includes the registration of the application to the Gateway, if the network is available.
         */
        this.start = function(successHandler, errorHandler) {
        
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "start", []);
        };

        this.startWithDefaultConfiguration = function(successHandler, errorHandler, defaultConfiguration) {
        
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "startWithDefaultConfiguration", [defaultConfiguration]);
        };

        this.startWithJSON = function(successHandler, errorHandler, jsonObject) {
         
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "startWithJSON", [jsonObject]);
        };

        this.authorize = function(successHandler, errorHandler, code) {
        
            Cordova.exec(successHandler, errorHandler, "MASPlugin", "authorizeQRCode", [code]);
        };

        /**
         Completes the current user's authentication session validation.
         * @param successHandler user defined success callback
         * @param errorHandler user defined error callback
         * @param username user defined username
         * @param password user defined password
        */
        this.completeAuthentication = function(successHandler, errorHandler, username, password) {
        
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "completeAuthentication", [username, password]);
        };

        /**
         Cancels the current user's authentication session validation.
         * @param successHandler user defined success callback
         * @param errorHandler user defined error callback
         * @param args user defined variable which is request Id in Android. It is not used in iOS
         */
        this.cancelAuthentication = function(successHandler, errorHandler, args) {
        
            $.mobile.activePage.find(".messagePopup").popup("close");
         
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "cancelAuthentication", [args]);
        };

        /**
         Request Server to generate and send OTP to the channels provided.
         * @param successHandler user defined success callback
         * @param errorHandler user defined error callback
         * @channels user defined variable which is an array of channels where the OTP is to be delivered.
         */
        this.generateAndSendOTP = function(successHandler, errorHandler, channels) {
            //$.mobile.activePage.find(".messagePopup").popup("close");
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "generateAndSendOTP", [channels]);
        };

        /**
         Cancels the current user's generating and sending OTP call.
         * @param successHandler user defined success callback
         * @param errorHandler user defined error callback
         */
        this.cancelGenerateAndSendOTP = function(successHandler, errorHandler) {
            $.mobile.activePage.find(".messagePopup").popup("close");
            //this.initialize(function() {});
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "cancelGenerateAndSendOTP", []);
        };
        /**
         Validate the entered OTP.
         * @param successHandler user defined success callback
         * @param errorHandler user defined error callback
         * @param otp user defined one time password that is to be verified
         */
        this.validateOTP = function(successHandler, errorHandler, otp) {
            $.mobile.activePage.find(".messagePopup").popup("close");
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "validateOTP", [otp]);
        };
        /**
         Cancels the current user's authentication session validation.
         * @param successHandler user defined success callback
         * @param errorHandler user defined error callback
         */
        this.cancelOTPValidation = function(successHandler, errorHandler) {
            $.mobile.activePage.find(".messagePopup").popup("close");
            //this.initialize(function() {});
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "cancelOTPValidation", []);
        };
        /**
         Closes an existing popup.
         * @param successHandler user defined success callback
         * @param errorHandler user defined error callback
         */
        this.closePopup = function() {
            $.mobile.activePage.find(".messagePopup").popup("close");
            return;
        };
        /**
         getFromPath does the HTTP GET call from the gateway. This expects atleast three mandatry parameters as shown in the the below example. The requestType and responseType are the optional parameters. If the requestType and responseType is not present then it is set to the Default Type to JSON.
         */
        this.getFromPath = function(successHandler, errorHandler, path, parametersInfo, headersInfo, requestType, responseType) {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "getFromPath", [path, parametersInfo, headersInfo, requestType, responseType]);
        };
        /**
         deleteFromPath does the HTTP DELTE call from the gateway. This expects atleast three mandatry parameters as shown in the the below example. The requestType and responseType are the optional parameters. If the requestType and responseType is not present then it is set to the Default Type to JSON.
         */
        this.deleteFromPath = function(successHandler, errorHandler, path, parametersInfo, headersInfo, requestType, responseType) {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "deleteFromPath", [path, parametersInfo, headersInfo, requestType, responseType]);
        };
        /**
         putToPath does the HTTP PUT call from the gateway. This expects atleast three mandatry parameters as shown in the the below example. The requestType and responseType are the optional parameters. If the requestType and responseType is not present then it is set to the Default Type to JSON.
         */
        this.putToPath = function(successHandler, errorHandler, path, parametersInfo, headersInfo, requestType, responseType) {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "putToPath", [path, parametersInfo, headersInfo, requestType, responseType]);
        };
        /**
         postToPath does the HTTP POST call from the gateway. This expects atleast three mandatry parameters as shown in the the below example. The requestType and responseType are the optional parameters. If the requestType and responseType is not present then it is set to the Default Type to JSON.
         */
        this.postToPath = function(successHandler, errorHandler, path, parametersInfo, headersInfo, requestType, responseType) {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "postToPath", [path, parametersInfo, headersInfo, requestType, responseType]);
        };
        /**
         Stops the lifecycle of all MAS processes.
         */
        this.stop = function(successHandler, errorHandler) {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "stop", []);
        };

        this.gatewayIsReachable = function(successHandler, errorHandler) {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "gatewayIsReachable", []);
        };
    },
    /**
     MASUser which has the interfaces mapped to the native MASUser class.
     */
    MASUser: function() {

        this.isSessionLocked = function(successHandler, errorHandler) {

            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "isSessionLocked", []); 
        };

        this.lockSession = function(successHandler, errorHandler) {

            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "lockSession", []); 
        }

        this.unlockSession = function(successHandler, errorHandler) {

            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "unlockSession", []); 
        }

        this.unlockSessionWithMessage = function(successHandler, errorHandler, message) {

            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "unlockSessionWithMessage", [message]); 
        }

        this.removeSessionLock = function(successHandler, errorHandler) {

            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "removeSessionLock", []); 
        }

        /**
         Authenticates the user using the username and password.
         */
        this.loginWithUsernameAndPassword = function(successHandler, errorHandler, username, password) {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "loginWithUsernameAndPassword", [username, password]);
        };
        /**
         * Performs an implicit login by calling an endpoint that requires authentication.
         */
        this.loginWithImplicitFlow = function(successHandler, errorHandler) {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "loginWithImplicitFlow", []);
        };
        /**
         log off user.
         */
        this.logoutUser = function(successHandler, errorHandler) {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "logoutUser", []);
        };
        /**
         *
         */
        this.getCurrentUser = function(successHandler, errorHandler) {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "getCurrentUser", []);
        };

        this.getUserName = function(successHandler, errorHandler) {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "getUserName", []);
        };
        /**
        Commented as they are related to SCIM as in Android, so Android implentation is not yet added.
       this.getName = function(successHandler, errorHandler)
        {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "getName", []);
        }

        this.getAddressList = function(successHandler, errorHandler)
        {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "getAddressList", []);
        }

        this.getEmailList = function(successHandler, errorHandler)
        {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "getEmailList", []);
        }

        this.isActive = function(successHandler, errorHandler)
        {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "isActive", []);
        }
        */

        this.isAuthenticated = function(successHandler, errorHandler) {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "isAuthenticated", []);
        };

    },

    /**
     *
     * MASAuthenticationProviders represents all available providers
     */
    MASAuthenticationProviders: function() {
        this.getCurrentProviders = function(successHandler, errorHandler) {
            return Cordova.exec(successHandler, errorHandler, "com.ca.apim.MASPlugin", "getCurrentProviders", []);
        };
        this.retrieveAuthenticationProviderForProximityLogin = function(successHandler, errorHandler) {
            return Cordova.exec(successHandler, errorHandler, "com.ca.apim.MASPlugin", "retrieveAuthenticationProviderForProximityLogin", []);
        };
    },

    /**
     MASDevice which has the interfaces mapped to the native MASDevice class.
     */
    MASDevice: function() {
        this.getCurrentDevice = function(successHandler, errorHandler) {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "getCurrentDevice", []);
        };
        /**
         Deregister the application resources on this device.
         */
        this.deregister = function(successHandler, errorHandler) {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "deregister", []);
        };
        this.resetLocally = function(successHandler, errorHandler) {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "resetLocally", []);
        };
        this.isDeviceRegistered = function(successHandler, errorHandler) {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "isDeviceRegistered", []);
        };
        this.getDeviceIdentifier = function(successHandler, errorHandler) {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "getDeviceIdentifier", []);
        };
    },
    /**
     *
     * MASApplication has the interfaces mapped to the native MASApplication class.
     */
    MASApplication: function() {
        /**
         this.getIdentifier = function(successHandler, errorHandler)
         {
             return Cordova.exec(successHandler, errorHandler, "MASPlugin", "getIdentifier",[]);
         };
         this.getName = function(successHandler, errorHandler,args)
         {
             return Cordova.exec(successHandler, errorHandler, "MASPlugin", "getName",[args]);
         };
         this.getIconUrl = function(successHandler, errorHandler)
         {
             return Cordova.exec(successHandler, errorHandler, "MASPlugin", "getIconUrl",[]);
         };
         this.getAuthUrl = function(successHandler, errorHandler)
         {
             return Cordova.exec(successHandler, errorHandler, "MASPlugin", "getAuthUrl",[]);
         };
         this.getNativeUri = function(successHandler, errorHandler)
         {
             return Cordova.exec(successHandler, errorHandler, "MASPlugin", "getNativeUri",[]);
         };
         this.getCustom = function(successHandler, errorHandler)
         {
             return Cordova.exec(successHandler, errorHandler, "MASPlugin", "getCustom",[]);
         };
         */
        this.retrieveEnterpriseApps = function(successHandler, errorHandler) {
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "retrieveEnterpriseApps", []);
        };
        this.launchApp = function(successHandler, errorHandler, appId) {
            document.addEventListener("backbutton", MASPlugin.MASConfig.onBackKeyPressEvent, false);
            return Cordova.exec(successHandler, errorHandler, "MASPlugin", "launchApp", [appId]);
        };
        this.initEnterpriseBrowser = function(successHandler, errorHandler) {
            var result = Cordova.exec(function(result) {
                MASPlugin.MASConfig.MASPopupUI(MASPlugin.MASConfig.enterpriseBrowserPage, function() {}, function() {
                    displayApps(result);
                    successHandler(true);
                });
            }, errorHandler, "MASPlugin", "retrieveEnterpriseApps", []);
            return result;
        };
    },


    /**
    MASConfig which is a singleton class used to store the state of the properties required.
    */
    MASConfig: {
        loginPage: "masui/mas-login.html",
        otpPage: "masui/mas-otp.html",
        otpChannelsPage: "masui/mas-otpchannel.html",
        enterpriseBrowserPage: "masui/mas-enterpriseBrowser.html",
        popUpStyle: "position: fixed; top: 50%; left:50%; transform: translate(-50%, -50%); width:47%; height: 55%; overflow: auto",
        loginAuthRequestId: "",
        onBackKeyPressEvent: function() {
            successHandler = function() {
                document.removeEventListener("backbutton", MASPlugin.MASConfig.onBackKeyPressEvent, false);
            };
            return Cordova.exec(successHandler, function() {}, "MASPlugin", "enterpriseBrowserWebAppBackButtonHandler", []);
        },
        setPopUpStyle: function(style){
            MASPlugin.MASConfig.popUpStyle=style;
        },
        getPopUpStyle: function(){
            return MASPlugin.MASConfig.popUpStyle;
        },
        MASPopupUI: function(url, popupafterclose, onload) {
            var onLoadMakePopUpVisible = function(){
                if(document.getElementById('popUp') !== null){
                    document.getElementById('popUp').hidden=false;
                 }
                 onload();
            };
            $('#popUp').remove();
            var template = "<div id='popUp' hidden data-role='popup' class='ui-content messagePopup' style='"+ MASPlugin.MASConfig.popUpStyle+"'>" + "</div>";
            popupafterclose = popupafterclose ? popupafterclose : function() {};
            $.mobile.activePage.append(template).trigger("create");

            $('#popUp').load(url, onLoadMakePopUpVisible);
            $.mobile.activePage.find(".closePopup").bind("tap", function() {
                $.mobile.activePage.find(".messagePopup").popup("close");
            });
            $.mobile.activePage.find(".messagePopup").popup().popup("open").bind({
                popupafterclose: function() {
                    $('body').off('touchmove');
                    $(this).unbind("popupafterclose").remove();
                    popupafterclose();
                }
            });
            $(".messagePopup").on({
                popupbeforeposition: function() {
                    $('.ui-popup-screen').off();
                }
            });
        },

        
        /** Used to send the username and password to the server and setting div with id "errorMesg" with error message if error occurs.
         * @param username user defined username
         * @param password user defined password
         */
        MASSendCredentials: function(username, password) {
            if (document.getElementById("errorMesg")) {
                document.getElementById("errorMesg").innerHTML = "";
            }
            var errorMsgToDisplay = "";
            var MAS = new MASPlugin.MAS();
            MAS.completeAuthentication(function() {
                if (document.getElementById("CA-Username") !== null ) {
                    $.mobile.activePage.find(".messagePopup").popup("close");
                }
            }, function(error) {
                if (typeof error !== 'undefined' && !isEmpty(error)) {
                    if (typeof error.errorCode !== 'undefined' && !isEmpty(error.errorCode) && !isNaN(error.errorCode)) {
                        var errorCodeLastDigits = error.errorCode % 1000;
                        var returnedError = "";
                        try {
                            if (typeof error.errorMessage !== 'undefined' && !isEmpty(error.errorMessage)) {
                                returnedError = JSON.parse(error.errorMessage);
                            }
                        } catch (e) {}
                        if (errorCodeLastDigits === 103) {
                            errorMsgToDisplay = "invalid request: Missing or duplicate parameters";
                            document.getElementById("errorMesg").innerHTML = errorMsgToDisplay;
                        } else if (errorCodeLastDigits === 202) {
                            errorMsgToDisplay = "Username or Password invalid";
                            document.getElementById("errorMesg").innerHTML = errorMsgToDisplay;
                        } else {
                            $.mobile.activePage.find(".messagePopup").popup("close");
                        }
                    }
                } else {
                    $.mobile.activePage.find(".messagePopup").popup("close");
                }
            }, username, password);
        },
        /**
         * Cancels the login reqest already made
         */
        MASCancelLogin: function() {
            var MAS = new MASPlugin.MAS();
            MAS.cancelAuthentication(function() {
                $.mobile.activePage.find(".messagePopup").popup("close");
            }, function(error) {}, MASPlugin.MASConfig.loginAuthRequestId);
        },
        
        /**
         * Used to send the OTP channels to the server
         * @param otpChannels user defined channels array that will be passed to server
         */
        MASSendOTPChannels: function(otpChannels, onError) {
            var MAS = new MASPlugin.MAS();
            MAS.generateAndSendOTP(
                function(shouldValidateOTP) {
                    $('#popUp').remove();
                    if ("true" == shouldValidateOTP) {
                        MASPlugin.MASConfig.MASPopupUI(MASPlugin.MASConfig.otpPage, function() {
                            $('#popUp').remove();
                        }, function() {});
                    }
                },
                function(error) {
                    if (document.getElementById("errorMesg")) {
                        var errorMsgToDisplay = "Internal Server Error.";
                        if (typeof error !== 'undefined' && !isEmpty(error) &&
                            typeof error.errorMessage !== 'undefined' && !isEmpty(error.errorMessage)
                        ) {
                            errorMsgToDisplay = error.errorMessage;
                        }
                        document.getElementById("errorMesg").innerHTML = errorMsgToDisplay;
                    } else {
                        onError(val);
                    }
                }, otpChannels);
        },
        
        /**
        * Used to send OTP for validation to the server
        @param otp user defined one time password that is passed to the server
        */
        MASSendOTPCredentials: function(otp) {
            var MAS = new MASPlugin.MAS();
            //MAS.initialize(function() {});
            MAS.validateOTP(function() {}, function() {}, otp);
        }
    }
};
    
module.exports = MASPlugin;