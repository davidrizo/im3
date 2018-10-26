import { Injectable } from '@angular/core';
/*import {PROJECTS} from './mock-projects';*/
import {Project} from '../model/project';
import { Observable, of } from 'rxjs';
import { MessageService } from '../messages/message.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import {isNull, isUndefined} from 'util';
import {Image} from '../model/image';


@Injectable({
  providedIn: 'root'
})

export class ProjectService {
  urlProject = 'http://localhost:8080/muret/project';  // URL to web api
  urlImage = 'http://localhost:8080/muret/image';  // URL to web api

  constructor(
    private http: HttpClient,
    private messageService: MessageService) { }

  public getProjects$(): Observable<Project[]> {
    this.log('ProjectService: fetching projects...');
    return this.http.get<Project[]>(this.urlProject)
      .pipe(
        catchError(this.handleError('getProjects$', []))
      );
  }

  public newProject$(name: string, comments: string, base64Thumbnail: string): Observable<Project> {
    this.log('ProjectService: creating project with name ' + name);
    return this.http.post<Project>(this.urlProject + '/new', {
      'name': name,
      'comments': comments,
      'thumbnailBase64Encoding': base64Thumbnail
    }).pipe(
        catchError(this.handleError('newProject$ with name=' + name, null))
      );
  }

  public getProject$(id: number): Observable<Project> {
    this.log('ProjectService: fetching project with id ' + id);
    return this.http.get<Project>(this.urlProject + '/get/' + id)
    .pipe(
        catchError(this.handleError('getProject$ with id=' + id, null))
      );
  }

  public getImage$(id: number): Observable<Image> {
    this.log('ProjectService: fetching image with id ' + id);
    return this.http.get<Image>(this.urlImage + '/get/' + id)
      .pipe(
        catchError(this.handleError('getImage$ with id=' + id, null))
      );
  }
  /** Log a message with the MessageService */
  private log(message: string) {
    this.messageService.add(`ProjectService: ${message}`);
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T> (operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}
