import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import {AuthService} from "../services/auth.service";
import {fadeAnimation, slideAnimation} from "../../core/animations/animations";
import {NotificationService} from "../services/notification.service";

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss'],
  animations: [fadeAnimation, slideAnimation]
})
export class SignupComponent implements OnInit {
  signupForm!: FormGroup;
  message: string | null = null;
  submitted = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  get f() { return this.signupForm.controls; }

  ngOnInit(): void {
    console.log("hello world ! sigup page");
    this.signupForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.signupForm.valid) {
      const { name, email, password } = this.signupForm.value;
      this.authService.signup({ name, email, password })
        .subscribe({
          next: (res) => {
            this.submitted = true;
            this.message = res.message;
            this.notificationService.success(this.message || 'Signup successful');
            this.router.navigate(['/verify-otp'], {
              queryParams: { email: this.signupForm.value.email }
            });
          },
          error: (err) => {
            this.message = err.error ? err.error.message : 'Signup failed';
            this.notificationService.error(this.message || 'Signup failed');
          }
        });
    }
  }

  onGoogleSignup() {
    window.location.href = 'http://localhost:8083/oauth2/authorization/google';
  }
}
