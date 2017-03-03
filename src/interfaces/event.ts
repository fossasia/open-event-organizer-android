import {ITicket} from "./ticket";

export interface ICallForPapers {
  start_date: Date;
  end_date: Date;
  privacy: string;
  announcement: string;
  timezone: string;
}

export interface ICopyright {
  logo: string;
  licence_url: string;
  holder_url: string;
  holder: string;
  licence: string;
  year: number;
}

export interface IVersion {
  speakers_ver: number;
  sponsors_ver: number;
  tracks_ver: number;
  microlocations_ver: number;
  event_ver: number;
  sessions_ver: number;
}

export interface ICreator {
  id: number;
  email: string;
}

export interface ISocialLink {
  link: string;
  id: number;
  name: string;
}

export interface IEvent {
  end_time: Date;
  sub_topic: string;
  start_time: Date;
  call_for_papers: ICallForPapers;
  searchable_location_name: string;
  description: string;
  longitude: number;
  copyright: ICopyright;
  location_name: string;
  type: string;
  organizer_name: string;
  latitude: number;
  code_of_conduct: string;
  version: IVersion;
  email: string;
  identifier: string;
  logo: string;
  timezone: string;
  creator: ICreator;
  name: string;
  background_image: string;
  social_links: ISocialLink[];
  topic: string;
  schedule_published_on: Date;
  id: number;
  organizer_description: string;
  state: string;
  ticket_url: string;
  privacy: string;
  event_url: string;
  has_session_speakers: boolean;
  tickets?: ITicket[];
}
