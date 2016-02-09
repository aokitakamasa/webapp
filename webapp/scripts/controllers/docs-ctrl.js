'use strict';

/**
 * @ngdoc function
 * @name panelApp.controller:DocsCtrl
 * @description
 * # DocsCtrl
 * Controller of the panelApp
 */
angular.module('panelApp')
  .controller('DocsCtrl', function(docsService) {

    var vm = this;

    vm.loading = true;
    vm.docs = [];

    docsService.then(function success(resp) {
      console.log(resp);
      vm.docs = resp.data;
      vm.loading = false;
    }, function error(resp) {
      console.log(resp);
    });

  });
