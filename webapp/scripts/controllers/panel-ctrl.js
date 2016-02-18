'use strict';

/**
 * @ngdoc function
 * @name panelApp.controller:PanelCtrl
 * @description
 * # PanelCtrl
 * Controller of the panelApp
 */
angular.module('panelApp')
  .controller('PanelCtrl', function($scope, $rootScope, $location, $anchorScroll, $timeout, restService, changeTerminalNameService) {
    // Variables
    //
    var vm = this;
    vm.merchant = null;
    vm.terminals = null;
    vm.services = null;

    restService.then(function success(resp) {
      vm.terminals = resp.data.terminals;
      vm.services = resp.data.services;
      vm.merchant = resp.data.merchant;
      $scope.main.loading = false;
    }, function error(resp) {
      console.log(resp.data);
    });


    vm.currentTerminal = null;
    vm.currentService = null;

    vm.currentInfo = null;
    // vm.currentInfo = 'editServices';

    vm.setCurrentTerminal = setCurrentTerminal;
    vm.setCurrentService = setCurrentService;
    vm.setCurrentInfo = setCurrentInfo;

    vm.hideDetails = hideDetails;
    vm.hideInfo = hideInfo;

    vm.terminalNameChanged = false;
    vm.changeTerminalName = changeTerminalName;

    vm.getFullServiceName = getFullServiceName;
    vm.scrollTo = scrollTo;

    // Methods
    //
    function setCurrentTerminal(terminal) {
      vm.currentService = null;
      if (typeof terminal === 'string') {
        terminal = JSON.parse(terminal);
      }
      vm.currentTerminal = terminal;
      console.log(vm.currentTerminal);
      scrollTo('detailsContainer');
    }

    function setCurrentService(service) {
      vm.currentTerminal = null;
      vm.currentService = service;
      scrollTo('detailsContainer');
    }

    function setCurrentInfo(subject) {
      vm.currentInfo = subject;
      scrollTo('infoContainer');
    }

    function hideDetails() {
      vm.currentTerminal = null;
      vm.currentService = null;
    }

    function hideInfo() {
      vm.currentInfo = null;
    }

    function changeTerminalName(name) {

      var terminalObj = {
        id: vm.currentTerminal.id,
        newName: name
      };

      changeTerminalNameService(terminalObj).then(function success(resp) {
        if (resp.status === 204) {
          for (var i = 0, len = vm.terminals.length; i < len; i++) {
            if (vm.terminals[i].id === terminalObj.id) {
              vm.terminals[i].name = terminalObj.newName;
            }
          }
          vm.terminalNameChanged = true;

          $timeout(function() {
            vm.terminalNameChanged = false;
            document.querySelector('[name="newTerminalName"]').value = null;
          }, 3000);
        }
      }, function error(resp) {
        console.log(resp);
      });
    }

    function getFullServiceName(service) {
      for (var i = 0; i < vm.services.length; i++) {
        if (vm.services[i].service === service) {
          return vm.services[i].name;
        }
      }
    }

    function scrollTo(target) {
      // Wait til target become available in DOM (ng-if)
      $timeout(function() {
        $location.hash(target);
        $anchorScroll();
      }, 0.1);
    }

  });
