import {Component, ErrorHandler} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {GlobalErrorHandler} from "./GlobalErrorHandler";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  providers:[
    { provide: ErrorHandler, useClass: GlobalErrorHandler }
  ]
})
export class AppComponent {
  title = 'frontend';

  constructor() {
    console.log('AppComponent created');
  }
}
