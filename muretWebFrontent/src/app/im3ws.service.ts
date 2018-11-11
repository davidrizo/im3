import { Injectable } from '@angular/core';
import {Project} from './model/project';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import {Image} from './model/image';
import {ConfigurationService} from './configuration.service';
import {ProjectURLS} from './model/project-urls';
import {NGXLogger} from 'ngx-logger';
import {StringReponse} from './string-reponse';
import {Scales} from './model/scales';

@Injectable({
  providedIn: 'root'
})

/**
 * This class has access to the IM3WS server, it includes the auth service
 */
export class Im3wsService {
  SESSION_USER_STORAGE = 'token';
  private urlProject: string;
  private urlImage: string;
  private urlSymbol: string;
  private urlLogin: string;
  private urlAuthenticatedUser: string;
  private urlAgnostic: string;

  username: string;
  isLoggedIn: boolean;


  constructor(
    private configurationService: ConfigurationService,
    private http: HttpClient,
    private logger: NGXLogger) {

    this.logger.debug('Creating Im3wsService');
    this.urlLogin = configurationService.IM3WS_SERVER + '/muret/auth/login';  // URL to web api
    this.urlProject = configurationService.IM3WS_SERVER + '/muret/project';  // URL to web api
    this.urlImage = configurationService.IM3WS_SERVER + '/muret/image';  // URL to web api
    this.urlSymbol = configurationService.IM3WS_SERVER + '/muret/symbol';  // URL to web api
    this.urlAgnostic = configurationService.IM3WS_SERVER + '/muret/agnostic';  // URL to web api
    this.urlAuthenticatedUser = configurationService.IM3WS_SERVER + '/muret/auth/user';  // URL to web api
  }

  logout(): void {
    // return this.im3wsService.logout();
    this.isLoggedIn = false;
    this.username = null;
    sessionStorage.removeItem(this.SESSION_USER_STORAGE);
  }

  authenticated(): boolean {
    return this.isLoggedIn;
  }

  login(username: string, password: string): Observable<boolean> {
    return this.http.post<boolean>(this.urlLogin, {
      username: username,
      password: password
    }).pipe(
      tap(isValid => {
        if (isValid) {
          sessionStorage.setItem(
            this.SESSION_USER_STORAGE,
            btoa(username + ':' + password)
          );

          this.isLoggedIn = true;
          this.username = username;
        }
      })
    );
  }

  getHttpAuthOptions() {
    const token = sessionStorage.getItem(this.SESSION_USER_STORAGE);
    if (!token) {
      console.log('User is not authorized yet');
      throw new Error('User is not authorized yet');
    }

    const headers: HttpHeaders = new HttpHeaders({
      'Authorization': 'Bearer ' + token
    });
    const options = {headers: headers};
    return options;


    /*const headers_object = new HttpHeaders();
    headers_object.append('Content-Type', 'application/json');
    headers_object.append('Authorization', 'Basic ' + btoa('username:password'));
    headers_object.append('Access-Control-Allow-Methods', 'GET, POST, DELETE, PUT');

    const httpOptions = {
      headers: headers_object
    };
    return httpOptions;*/
  }

  checkAuthorized() {
    this.http.post<Observable<Object>>(this.urlAuthenticatedUser, {}, this.getHttpAuthOptions()).
    subscribe(principal => {
        console.log('Current user: ' + principal['name']);
      },
      error => {
        if (error.status === 401) {
          alert('Unauthorized');
        }
      }
    );
  }

    /*this.http.post<Observable<boolean>>(this.urlLogin, {
      username: username,
      password: password
    }).subscribe(isValid => {
      if (isValid) {
        sessionStorage.setItem(
          'token',
          btoa(username + ':' + password)
        );
        // router.navigate(['']);
        return true;
      } else {
        // alert('Authentication failed.');
        return false;
      }
    });*/


  public getProjects$(): Observable<Project[]> {
    this.logger.debug('IM3WSService: fetching projects...');

    return this.http.get<Project[]>(this.urlProject, this.getHttpAuthOptions())
      .pipe(
        catchError(this.handleError('getProjects$', []))
      );
  }

  public newProject$(name: string, composer: string, notationType: string, manuscriptType: string, comments: string,
                     base64Thumbnail: string): Observable<Project> {
    this.logger.debug('IM3WSService: creating project with name ' + name);
    return this.http.post<Project>(this.urlProject + '/new', {
      'name': name,
      'composer': composer,
      'notationType': notationType,
      'manuscriptType': manuscriptType,
      'comments': comments,
      'thumbnailBase64Encoding': base64Thumbnail
    }, this.getHttpAuthOptions()).pipe(
        catchError(this.handleError('newProject$ with name=' + name, null))
      );
  }

  public getProject$(id: number): Observable<Project> {
    this.logger.debug('IM3WSService: fetching project with id ' + id);
    const result: Observable<Project> = this.http.get<Project>(this.urlProject + '/get/' + id, this.getHttpAuthOptions())
    .pipe(
        catchError(this.handleError('getProject$ with id=' + id, null))
      );
    this.logger.debug('IM3WSService: fetched ' + result.valueOf());
    return result;
  }

  public getProjectURLs$(id: number): Observable<ProjectURLS> {
    this.logger.debug('IM3WSService: fetching thumbnail URL of project with id ' + id);
    const result: Observable<ProjectURLS> = this.http.get<ProjectURLS>(this.urlProject + '/projectURLS/' + id, this.getHttpAuthOptions())
      .pipe(
        catchError(this.handleError('getProject$ with id=' + id, null))
      );
    this.logger.debug('IM3WSService: fetched URL ' + result.valueOf());
    return result;
  }

  public getImage$(id: number): Observable<Image> {
    this.logger.debug('IM3WSService: fetching image with id ' + id);
    return this.http.get<Image>(this.urlImage + '/get/' + id, this.getHttpAuthOptions())
      .pipe(
        catchError(this.handleError('getImage$ with id=' + id, null))
      );
  }
  // TODO ¿Se está usando? - Si no, quitarlo
  public getSymbolsOfRegion$(regionID: number): Observable<Symbol> {
    this.logger.debug('IM3WSService: fetching symbols with region_id ' + regionID);
    return this.http.get<Symbol>(this.urlSymbol + '/region/' + regionID, this.getHttpAuthOptions())
      .pipe(
        catchError(this.handleError('getSymbols$ with region_id=' + regionID, null))
      );
  }
  public getSVGFromAgnosticSymbolType$(notationType: string, manuscriptType: string, agnosticSymbolType: string)
    : Observable<StringReponse> {
    this.logger.debug('IM3WSService: fetching svg path for notationType=' + notationType
      + 'manuscriptType=' + manuscriptType
      + ' and agnosticSymbolType=' + agnosticSymbolType);
    return this.http.get<StringReponse>(this.urlAgnostic + '/svg'
      + '?notationType=' + notationType
      + '&manuscriptType=' + manuscriptType
      + '&symbolType=' + agnosticSymbolType
      ,
      this.getHttpAuthOptions(),
      )
      .pipe(
        catchError(this.handleError('getSVGFromAgnosticSymbolType ' + agnosticSymbolType, null))
      );
  }

  public getSVGScales$(notationType: string, manuscriptType: string): Observable<Scales> {
    this.logger.debug('IM3WSService: fetching svg scale for notationType=' + notationType
      + 'manuscriptType=' + manuscriptType);
    return this.http.get<StringReponse>(this.urlAgnostic + '/svgscales'
      + '?notationType=' + notationType
      + '&manuscriptType=' + manuscriptType
      ,
      this.getHttpAuthOptions(),
    )
      .pipe(
        catchError(this.handleError('getSVGScale', null))
      );
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
      this.logger.error(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  public saveProject(project: Project): Observable<any> {
    const result = this.http.put(this.urlProject, project, this.getHttpAuthOptions());
    result.subscribe(res => {
      console.log('Save project result: ' + res);
    });
    return result;
  }

}
