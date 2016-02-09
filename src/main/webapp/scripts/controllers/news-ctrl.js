'use strict';

/**
 * @ngdoc function
 * @name panelApp.controller:NewsCtrl
 * @description
 * # NewsCtrl
 * Controller of the panelApp
 */
angular.module('panelApp')
  .controller('NewsCtrl', function(newsService) {

    var vm = this;

    vm.loading = true;
    vm.news = [];

    newsService.then(function success(resp) {
      console.log(resp);
      vm.news = resp.data;
      vm.loading = false;
    }, function error(resp) {
      console.log(resp);
    });
  });
