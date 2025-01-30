import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {AuthService} from "../services/auth.service";
import {fadeAnimation, slideAnimation} from "../../core/animations/animations";

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss'],
  animations: [fadeAnimation, slideAnimation]
})
export class ResetPasswordComponent implements OnInit {
  resetForm!: FormGroup;
  message: string | null = null;
  token: string | null = null;
  submitted=false

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.resetForm = this.fb.group({
      newPassword: ['', Validators.required]
    });

    // read token from URL
    this.route.queryParams.subscribe(params => {
      this.token = params['token'] || null;
    });
  }

  onSubmit() {
    if (this.resetForm.valid && this.token) {
      const { newPassword } = this.resetForm.value;
      this.authService.resetPassword(this.token, newPassword).subscribe({
        next: (res) => {
          this.submitted = true;
          this.message = res.message;
          setTimeout(() => this.router.navigate(['/login']), 2000);
        },
        error: (err) => {
          this.message = err.error ? err.error : 'Reset failed';
        }
      });
    }
  }
}
