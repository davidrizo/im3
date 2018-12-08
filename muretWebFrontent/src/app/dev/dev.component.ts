import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';
import {Im3wsService} from '../im3ws.service';
import {SessionDataService} from '../session-data.service';

@Component({
  selector: 'app-dev',
  templateUrl: './dev.component.html',
  styleUrls: ['./dev.component.css']
})

// Used to speed up development
export class DevComponent implements OnInit {

  constructor(private im3wsService: Im3wsService, private router: Router,
              private sessionDataService: SessionDataService) {
    console.warn('¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡ DEV!!!!!!!!!!!!!!!!!!!!!!');
    this.im3wsService.login('davidrizo', 'nose').subscribe(
      next => {
        if (next) {
          this.im3wsService.setUser(next);
          this.router.navigate(['/project/37'])
            .then(value => {
              this.im3wsService.getImage$(198).
              subscribe(serviceImage => {
                this.sessionDataService.currentImageMastersURL
                  = 'http://localhost:8888/muret/villancico-al-smo--sto--al-molino-del-amor/masters/';
                this.sessionDataService.currentImage = serviceImage;
                this.router.navigate(['/image']);
              });
            });
        } else {
          throw new Error('Cannot authenticate!!!');
        }
      }
    );
  }

  ngOnInit() {
  }

}
