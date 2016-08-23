// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.services' is found in services.js
// 'starter.controllers' is found in controllers.js

require('./config');
require('./controllers');
require('./directives');
require('./routes');
require('angular-jwt');
require('./services');

angular.module('app', ['ionic', 'angular-jwt', 'app.config', 'app.controllers', 'app.routes', 'app.directives', 'app.services'])
    .config(function Config($httpProvider, jwtOptionsProvider, APP_CONFIG) {
        jwtOptionsProvider.config({
            unauthenticatedRedirectPath: '/login',
            whiteListedDomains: APP_CONFIG.WHITELISTED_DOMAINS,
            tokenGetter: ['options', function (options) {
                // Skip authentication for template requests
                if (options.url.substr(options.url.length - 5) == '.html') {
                    return null;
                }
                return localStorage.getItem('access_token');
            }]
        });
        $httpProvider.interceptors.push('jwtInterceptor');
    })
    .run(function ($ionicPlatform, authManager) {
        authManager.redirectWhenUnauthenticated();
        authManager.checkAuthOnRefresh();
        $ionicPlatform.ready(function () {
            // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
            // for form inputs)
            if (window.cordova && window.cordova.plugins && window.cordova.plugins.Keyboard) {
                cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
                cordova.plugins.Keyboard.disableScroll(true);
            }
            if (window.StatusBar) {
                // org.apache.cordova.statusbar required
                //noinspection NodeModulesDependencies
                StatusBar.styleDefault();
            }
        });
    });
