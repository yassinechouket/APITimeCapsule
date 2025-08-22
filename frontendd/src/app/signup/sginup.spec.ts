import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Sginup } from './sginup';

describe('Sginup', () => {
  let component: Sginup;
  let fixture: ComponentFixture<Sginup>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Sginup]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Sginup);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
