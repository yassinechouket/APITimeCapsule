import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthenticationRequest } from '../services/models';
import { AuthenticationControllerService } from '../services/services';
import { Token } from '../services/token/token';
import { HttpClientModule } from '@angular/common/http';


@Component({
  selector: 'app-user-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule,HttpClientModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class UserLoginComponent implements OnInit {
  loginForm: FormGroup;
  isLoading = false;
  showPassword =false;
  authRequest: AuthenticationRequest={email:'',password:''}
  errorMsg:Array<string>=[]

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthenticationControllerService,
    private tokenService:Token,
   
  ) {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rememberMe: [false]
    });
  }

  ngOnInit(): void {}

  
  login():void{
    this.errorMsg=[];

    this.authRequest.email = this.loginForm.value.email;
    this.authRequest.password = this.loginForm.value.password;


    this.authService.authenticate({
      body:this.authRequest
    }).subscribe({
      next:(res)=>{
        console.log('Login successful:', res);
        this.tokenService.token=res.token as string;
        this.isLoading = false;
        this.router.navigate(['/email']);
      },
      error:(err)=>{
        console.error('Login failed:', err);
        this.errorMsg.push('Invalid email or password.');
        this.isLoading = false;
        if(err.error.validationErrors){
          this.errorMsg=err.error.validationErrors
        }
        else{
          this.errorMsg.push(err.errorMsg)
        }
      }
    });

  }

  onGoogleLogin(): void {
    console.log('Login with Google');
  }

  onForgotPassword(): void {
    console.log('Forgot password clicked');
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  goToSignUp(): void {
    this.router.navigate(['/signup']);
  }

  get email() { return this.loginForm.get('email'); }
  get password() { return this.loginForm.get('password'); }
}