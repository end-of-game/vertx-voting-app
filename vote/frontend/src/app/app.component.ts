import { Component, OnInit } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import 'rxjs/add/operator/toPromise';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  public hostname: String = "";
  private choices: String[] = [];
  private myChoice: String = "";
  constructor(private http: Http) { }
   
  ngOnInit() {
    this.getInfo().then(data => {
      this.hostname = data.hostname;
      this.choices = data.choice;
      console.log(this.choices);
    });
  }

  public getInfo(): any {
    return this.http.get('http://localhost:8080/vote',
      {headers: new Headers({'Content-Type': 'application/json'}), withCredentials: true}
    ).toPromise()
      .then ( data => { console.log(data); return data.json(); })
      .catch(err => console.log(err) );
  }
  public choose(choice: String): void {
    this.myChoice = choice;
    console.log(this.myChoice);
    this.http.post('http://localhost:8080/vote',
      JSON.stringify({vote: choice}),
      {headers: new Headers({'Content-Type': 'application/json'}), withCredentials: true}
    ).subscribe(
      data => {
        console.log(data);
      },
      err => { console.log(err) }
    );
  }
}
