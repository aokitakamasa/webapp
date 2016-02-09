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
    $scope.merchant = null;
    $scope.terminals = null;
    $scope.services = null;

    restService.then(function success(resp) {
      $scope.terminals = resp.data.terminals;
      $scope.services = resp.data.services;
      $scope.merchant = resp.data.merchant;
      $scope.main.loading = false;
    }, function error(resp) {
      console.log(resp.data);
    });


    $scope.currentTerminal = null;
    $scope.currentService = null;

    $scope.currentInfo = null;

    $scope.setCurrentTerminal = setCurrentTerminal;
    $scope.setCurrentService = setCurrentService;
    $scope.setCurrentInfo = setCurrentInfo;

    $scope.hideDetails = hideDetails;
    $scope.hideInfo = hideInfo;

    $scope.terminalNameChanged = false;
    $scope.changeTerminalName = changeTerminalName;

    $scope.getFullServiceName = getFullServiceName;

    // Methods
    //
    function setCurrentTerminal(terminal) {
      $scope.currentService = null;
      $scope.currentTerminal = terminal;
      scrollTo('detailsContainer');
    }

    function setCurrentService(service) {
      $scope.currentTerminal = null;
      $scope.currentService = service;
      scrollTo('detailsContainer');
    }

    function setCurrentInfo(subject) {
      $scope.currentInfo = subject;
      scrollTo('infoContainer');
    }

    function hideDetails() {
      $scope.currentTerminal = null;
      $scope.currentService = null;
    }

    function hideInfo() {
      $scope.currentInfo = null;
    }

    function changeTerminalName(name) {

      var terminalObj = {
        id: $scope.currentTerminal.id,
        newName: name
      };

      changeTerminalNameService(terminalObj).then(function success(resp) {
        if (resp.status === 204) {
          for (var i = 0, len = $scope.terminals.length; i < len; i++) {
            if ($scope.terminals[i].id === terminalObj.id) {
              $scope.terminals[i].name = terminalObj.newName;
            }
          }
          $scope.terminalNameChanged = true;

          $timeout(function() {
            $scope.terminalNameChanged = false;
            document.querySelector('[name="newTerminalName"]').value = null;
          }, 3000);
        }
      }, function error(resp) {
        console.log(resp);
      });
    }

    function getFullServiceName(service) {
      for (var i = 0; i < $scope.services.length; i++) {
        if ($scope.services[i].service === service) {
          return $scope.services[i].name;
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
