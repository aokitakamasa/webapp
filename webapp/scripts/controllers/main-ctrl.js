'use strict';

/**
 * @ngdoc function
 * @name panelApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the panelApp
 */
angular.module('panelApp')
  .controller('MainCtrl', function($scope, authService) {
    var vm = this;
    vm.loading = true;
    vm.logout = authService.logout;
  });
