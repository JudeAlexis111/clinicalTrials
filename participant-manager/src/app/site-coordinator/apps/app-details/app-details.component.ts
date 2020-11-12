import {Component, OnInit, TemplateRef} from '@angular/core';
import {AppDetailsService} from '../shared/app-details.service';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {ActivatedRoute} from '@angular/router';
import {BehaviorSubject, Observable, of, combineLatest} from 'rxjs';
import {AppDetails, Participant, EnrolledStudy} from '../shared/app-details';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
import {map} from 'rxjs/operators';
import {Status, StudyType} from 'src/app/shared/enums';
import {SearchService} from 'src/app/shared/search.service';

@Component({
  selector: 'app-app-details',
  templateUrl: './app-details.component.html',
  styleUrls: ['./app-details.component.scss'],
})
export class AppDetailsComponent
  extends UnsubscribeOnDestroyAdapter
  implements OnInit {
  appId = '';
  query$ = new BehaviorSubject('');
  appDetail$: Observable<AppDetails> = of();
  appDetailsBackup = {} as AppDetails;
  statusEnum = Status;
  enrolledStudies: EnrolledStudy[] = [];
  studyTypes = StudyType;

  constructor(
    private readonly modalService: BsModalService,
    public modalRef: BsModalRef,
    private readonly appDetailsService: AppDetailsService,
    private readonly route: ActivatedRoute,
    private readonly sharedService: SearchService,
  ) {
    super();
  }

  openModal(
    appEnrollList: TemplateRef<unknown>,
    enrolledStudies: EnrolledStudy[],
  ): void {
    this.enrolledStudies = enrolledStudies;
    if (enrolledStudies.length > 0) {
      this.modalRef = this.modalService.show(appEnrollList);
    }
  }

  ngOnInit(): void {
    this.sharedService.updateSearchPlaceHolder('Search Participant Email');
    this.subs.add(
      this.route.params.subscribe((params) => {
        if (params.appId) {
          this.appId = params.appId as string;
        }
        this.fetchParticipantsDetails();
      }),
    );
  }

  fetchParticipantsDetails(): void {
    this.appDetail$ = combineLatest(
      this.appDetailsService.get(this.appId),
      this.query$,
    ).pipe(
      map(([appDetails, query]) => {
        this.appDetailsBackup = JSON.parse(
          JSON.stringify(appDetails),
        ) as AppDetails;
        this.appDetailsBackup.participants = this.appDetailsBackup.participants.filter(
          (participant: Participant) =>
            participant.email?.toLowerCase().includes(query.toLowerCase()),
        );
        return this.appDetailsBackup;
      }),
    );
  }

  search(query: string): void {
    this.query$.next(query.trim());
  }
}