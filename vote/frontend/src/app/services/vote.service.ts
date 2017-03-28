import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs/Rx'
import { Http, Response, Headers } from '@angular/http';

// import { HttpService } from '../core/http.service';
import { Vote } from '../models/vote';
import { environment } from './../../environments/environment';

@Injectable()
export class VoteService {

  private BASE_URL: string = environment.API_BASE_URL;

  constructor(private _http: Http) { }
  
  public getInfo(): Promise<Vote> {  
    return this._http.get(this.BASE_URL + '/vote',
      {headers: new Headers({'Content-Type': 'application/json'}), withCredentials: true}
    ).toPromise()
      .then ( data => { return data.json(); })
      .catch(err => console.log(err) );
  }
  
  public postChoice(vote): Promise<any> {
    return this._http.post(this.BASE_URL + '/vote',
      JSON.stringify(vote),
      {headers: new Headers({'Content-Type': 'application/json'}), withCredentials: true}
    ).toPromise()
      .then ( data => { return data.json(); })
      .catch(err => console.log(err) );
  }

}
