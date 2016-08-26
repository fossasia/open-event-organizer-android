// The following are the constructor functions for each page's controller. See https://docs.angularjs.org/guide/controller
// You can include any angular dependencies as parameters for this function
// TIP: Access Route Parameters for your page via $stateParams.parameterName

module.exports = angular.module('app.controllers', []);

require('./controllers/**/*.js', {mode: 'expand'});

