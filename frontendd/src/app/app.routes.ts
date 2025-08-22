import { Routes } from '@angular/router';
import{UserLoginComponent} from './login/login';
import{UserRegisterComponent} from './signup/signup';
import { Email } from './email/email';


export const routes: Routes = [
  
  { path: '', redirectTo: '/signup', pathMatch: 'full' },
  
  
  { path: 'login', component: UserLoginComponent },
  { path: 'signup', component: UserRegisterComponent },
  {path:'email', component:Email},
  
 
  
  
  
  { path: '**', redirectTo: '/signup' }
];