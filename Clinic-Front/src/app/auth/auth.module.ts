import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { VerifyOtpComponent } from './verify-otp/verify-otp.component';
import {ReactiveFormsModule} from "@angular/forms";
import { HttpClientModule } from '@angular/common/http';
import {MatCard, MatCardContent, MatCardModule, MatCardTitle} from "@angular/material/card";
import {MatFormField, MatFormFieldModule} from "@angular/material/form-field";
import {MatProgressSpinner, MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {RouterLink} from "@angular/router";
import {AuthRoutingModule} from "./auth-routing.module";

@NgModule({
  declarations: [
    LoginComponent,
    SignupComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent,
    VerifyOtpComponent
  ],
  imports: [
    BrowserAnimationsModule, // needed for Angular Material animations
    CommonModule,
    ReactiveFormsModule,
    HttpClientModule,

    // Material modules here:
    MatCardModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
    MatInputModule,
    MatButtonModule,
    RouterLink,
    AuthRoutingModule
  ]
})
export class AuthModule { }
