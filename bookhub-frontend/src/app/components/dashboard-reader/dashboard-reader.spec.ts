import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardReader } from './dashboard-reader';

describe('DashboardReader', () => {
  let component: DashboardReader;
  let fixture: ComponentFixture<DashboardReader>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardReader]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DashboardReader);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
