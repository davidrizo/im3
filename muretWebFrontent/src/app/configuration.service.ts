import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {MessageService} from './messages/message.service';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {Configuration} from './configuration';

@Injectable({
  providedIn: 'root'
})


export class ConfigurationService {
  private urlConfiguration: string;
  public IM3WS_SERVER = 'http://localhost:8080'; // TODO A fichero de configuración

  constructor(
    private http: HttpClient,
    private messageService: MessageService) {
  }
}
