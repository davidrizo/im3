import { Component, OnInit } from '@angular/core';
import {Im3wsService} from '../../im3ws.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  constructor(private im3WSService: Im3wsService) {
  }
  authenticated(): boolean {
    return this.im3WSService.authenticated();
  }
}
