import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RegisterRequest } from '../services/models';
import { AuthenticationControllerService } from '../services/services';
import { HttpClientModule } from '@angular/common/http';




@Component({
  selector: 'app-user-register',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule,HttpClientModule],
  providers: [AuthenticationControllerService],
  templateUrl: './signup.html',
  styleUrls: ['./signup.scss']   
})
export class UserRegisterComponent implements OnInit {
  registerForm: FormGroup;
  isLoading = false;
  showPassword = false;
  showConfirmPassword = false;
  registerRequest :RegisterRequest={email:'',username:'',password:''};
  errorMsg:Array<string>=[];

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthenticationControllerService,

  ) {
    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
      agreeToTerms: [false, [Validators.requiredTrue]]
    }, { validators: this.passwordMatchValidator });
  }

  ngOnInit(): void {}

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    
    if (confirmPassword?.errors?.['passwordMismatch']) {
      delete confirmPassword.errors['passwordMismatch'];
      if (Object.keys(confirmPassword.errors).length === 0) {
        confirmPassword.setErrors(null);
      }
    }
    
    return null;
  }

  register(): void {
    this.errorMsg = [];
    this.isLoading = true;

    this.registerRequest = {
      email: this.email?.value,
      username: this.username?.value,
      password: this.password?.value
    };
    
    
  
    this.authService.register({body:this.registerRequest}).subscribe({
      next: (res) => {
        console.log('register successful:', res);
        this.isLoading = false;
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.isLoading = false;
        if (err.error && err.error.validationErrors) {
          this.errorMsg = err.error.validationErrors;
        } else if (err.error && err.error.message) {
          this.errorMsg = [err.error.message];
        } else {
          this.errorMsg = ['Registration failed. Please try again.'];
        }
        
        console.error('Registration error:', err);
      }
    });
      }
    
  


  

  onGoogleRegister(): void {
    console.log('Register with Google');
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  
  get username(){return this.registerForm.get('username')}
  get email() { return this.registerForm.get('email'); }
  get password() { return this.registerForm.get('password'); }
  get confirmPassword() { return this.registerForm.get('confirmPassword'); }
  get agreeToTerms() { return this.registerForm.get('agreeToTerms'); }
}