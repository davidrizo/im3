import { Component } from '@angular/core';
import { Router,
  NavigationExtras } from '@angular/router';
import {Im3wsService} from '../im3ws.service';
import {NGXLogger} from 'ngx-logger';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  message: string;
  model: any = {};

  constructor(private im3WSService: Im3wsService, private router: Router, private logger: NGXLogger) {
    this.setMessage();
  }

  setMessage() {
    this.message = 'Logged ' + (this.im3WSService.isLoggedIn ? 'in' : 'out') + ' as ' + this.im3WSService.username;
  }

  login() {
    this.logger.debug('Loging in');
    this.message = 'Trying to log in ...';

    this.im3WSService.login(this.model.username, this.model.password).subscribe(() => {
      this.setMessage();
      if (this.im3WSService.isLoggedIn) {
        const redirect = 'startup';
        // Redirect the user
        this.router.navigate([redirect]);
      }
    });
  }

  logout() {
    this.logger.debug('Loging out');
    this.im3WSService.logout();
    this.setMessage();
  }

  isLoggedIn(): boolean {
    return this.im3WSService.isLoggedIn;
  }
}


/*
Copyright 2017-2018 Google Inc. All Rights Reserved.
Use of this source code is governed by an MIT-style license that
can be found in the LICENSE file at http://angular.io/license
*/
