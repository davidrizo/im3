import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {MessageService} from './messages/message.service';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {Configuration} from './configuration';
import {environment} from '../environments/environment';

@Injectable({
  providedIn: 'root'
})


export class ConfigurationService {
  private urlConfiguration: string;
  public IM3WS_SERVER = environment.apiEndpoint; //  = 'http://localhost:8080';

  constructor(
    private http: HttpClient,
    private messageService: MessageService) {
  }
}
