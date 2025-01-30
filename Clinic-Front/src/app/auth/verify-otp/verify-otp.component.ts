import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription, interval } from 'rxjs';
import { take } from 'rxjs/operators';
import {AuthService} from "../services/auth.service";
import {NotificationService} from "../services/notification.service";

@Component({
  selector: 'app-verify-otp',
  templateUrl: './verify-otp.component.html',
  styleUrls: ['./verify-otp.component.scss']
})
export class VerifyOtpComponent implements OnInit, OnDestroy {
  otpForm!: FormGroup;
  loading = false;
  email: string | null = null;
  countdown = 60;
  private timerSubscription!: Subscription;
  canResend = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.otpForm = this.fb.group({
      otp: ['', Validators.required]
    });

    // read email from query params if provided
    this.route.queryParams.subscribe(params => {
      this.email = params['email'] || null;
    });

    // start countdown
    this.startCountdown(60);
  }

  startCountdown(seconds: number) {
    this.countdown = seconds;
    this.canResend = false;

    // create an interval that emits every second
    this.timerSubscription = interval(1000).pipe(take(seconds)).subscribe({
      next: () => {
        this.countdown--;
        if (this.countdown === 0) {
          this.canResend = true;
        }
      }
    });
  }

  onSubmit() {
    if (this.otpForm.invalid) return;

    this.loading = true;
    const { otp } = this.otpForm.value;

    this.authService.verifyOtp(otp).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.status === 200) {
          this.notificationService.success('Account Verified! You can now login.');
          this.router.navigate(['/login']);
        } else {
          this.notificationService.error(`Unexpected status: ${res.status}`);
        }
      }
    });

  }


  onResend() {
    if (!this.email) {
      this.notificationService.error('Cannot resend OTP without email');
      return;
    }
    this.loading = true;
    // Suppose your backend has an endpoint /resend-otp?email=XYZ
    /*
    this.authService.resendOtp(this.email).subscribe({
      next: (res) => {
        this.loading = false;
        this.notificationService.info('OTP resent. Check your email.');
        this.startCountdown(60);
      },
      error: (err) => {
        this.loading = false;
        this.notificationService.error(err.error?.message || 'Failed to resend OTP');
      }
    });*/
  }

  ngOnDestroy(): void {
    if (this.timerSubscription) {
      this.timerSubscription.unsubscribe();
    }
  }
}
