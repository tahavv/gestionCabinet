import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import {AuthService} from "../services/auth.service";
import {NotificationService} from "../services/notification.service";
import {fadeAnimation, slideAnimation} from "../../core/animations/animations";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  animations: [fadeAnimation, slideAnimation]
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  loading = false;
  submitted = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    if(this.authService.isLoggedIn()){
      this.router.navigate(['/admin']);
    }
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  get f() { return this.loginForm.controls; }

  onSubmit() {
    if (this.loginForm.invalid) return;
    this.loading = true;
    const { email, password } = this.loginForm.value;
    this.authService.login(email, password).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.token) {
          localStorage.setItem('token', res.token);
          this.authService.setUser(res.user); // Save user info
          this.notificationService.success('Login successful!');
          this.router.navigate(['/admin']);
        }
      },
      error: (err) => {
        this.loading = false;
        this.notificationService.error(err.error?.message || 'Login failed');
      },
    });
  }

  onGoogleLogin() {
    // Redirect to your backend endpoint for Google OAuth2
    window.location.href = 'http://localhost:8083/oauth2/authorization/google';
  }
}
