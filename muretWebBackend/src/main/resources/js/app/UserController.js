'use strict'

var module = angular.module('demo.controllers', []);
module.controller("UserController", [ "$scope", "UserService",
    function($scope, UserService) {

        $scope.userDto = {
            userId : null,
            userName : null,
            skillDtos : []
        };
        $scope.skills = [];

        UserService.getUserById(1).then(function(value) {
            console.log(value.data);
        }, function(reason) {
            console.log("error occured");
        }, function(value) {
            console.log("no callback");
        });

        $scope.saveUser = function() {
            UserService.saveUser($scope.userDto).then(function() {
                console.log("works");
                UserService.getAllUsers().then(function(value) {
                    $scope.allUsers= value.data;
                }, function(reason) {
                    console.log("error occured");
                }, function(value) {
                    console.log("no callback");
                });

                $scope.skills = [];
                $scope.userDto = {
                    userId : null,
                    userName : null,
                    skillDtos : []
                };
            }, function(reason) {
                console.log("error occured");
            }, function(value) {
                console.log("no callback");
            });
        }
    } ]);