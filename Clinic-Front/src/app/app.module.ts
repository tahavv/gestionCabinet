import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {AuthModule} from "./auth/auth.module";
import {HTTP_INTERCEPTORS} from "@angular/common/http";
import {TokenInterceptor} from "./core/interceptors/token.interceptor";
import { HomeComponent } from './core/pages/home/home.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ToastrModule } from 'ngx-toastr';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import { AdminDashboardComponent } from './core/pages/admin-dashboard/admin-dashboard.component';
import { AdminModule } from './admin/admin.module';
import {RouterModule} from "@angular/router";
import {LandingModule} from "./landing/landing.module";

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    AdminDashboardComponent
  ],
  imports: [
    BrowserAnimationsModule,
    BrowserModule,
    AppRoutingModule,
    RouterModule,
    AuthModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    ToastrModule.forRoot({
      positionClass: 'toast-top-right',
      timeOut: 3000,
      preventDuplicates: true,
      closeButton: true,
      progressBar: true,
      extendedTimeOut: 1000
    }),
    AdminModule,
    LandingModule,
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true},
    provideAnimationsAsync()
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
