import { Component, OnInit } from '@angular/core';
import {Im3wsService} from '../../im3ws.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  hideLogoIn = new Set([
    '/about',
    '/startup'
    ]);

  constructor(private im3WSService: Im3wsService, private router: Router) {
  }
  authenticated(): boolean {
    return this.im3WSService.authenticated();
  }

  hideLogo(): boolean {
    return this.hideLogoIn.has(this.router.url);
  }
}
