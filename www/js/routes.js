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
            .state('event_picker', {
                url: '/',
                templateUrl: 'templates/event_picker.html',
                controller: 'eventPickerCtrl'
            })
            .state('event_dashboard', {
                url: '/event',
                templateUrl: 'templates/event/dashboard.html',
                controller: 'eventDashboardCtrl'
            });
        $urlRouterProvider.otherwise('/login')
    });
