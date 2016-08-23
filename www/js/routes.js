
// Ionic uses AngularUI Router which uses the concept of states
// Learn more here: https://github.com/angular-ui/ui-router
// Set up the various states which the app can be in.
// Each state's controller can be found in controllers.js

module.exports = angular.module('app.routes', [])
    .config(function ($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('login', {
                url: '/login',
                templateUrl: 'templates/login.html',
                skipAuthorization: true,
                controller: 'loginCtrl'
            })
            .state('dashboard', {
                url: '/',
                templateUrl: 'templates/dashboard.html',
                controller: 'dashboardCtrl'
            });
        $urlRouterProvider.otherwise('/login')
    });
