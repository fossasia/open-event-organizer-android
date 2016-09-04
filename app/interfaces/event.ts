import {Ticket} from "./ticket";

export interface CallForPapers {
  start_date: Date;
  end_date: Date;
  privacy: string;
  announcement: string;
  timezone: string;
}

export interface Copyright {
  logo: string;
  licence_url: string;
  holder_url: string;
  holder: string;
  licence: string;
  year: number;
}

export interface Version {
  speakers_ver: number;
  sponsors_ver: number;
  tracks_ver: number;
  microlocations_ver: number;
  event_ver: number;
  sessions_ver: number;
}

export interface Creator {
  id: number;
  email: string;
}

export interface SocialLink {
  link: string;
  id: number;
  name: string;
}

export interface Event {
  end_time: Date;
  sub_topic: string;
  start_time: Date;
  call_for_papers: CallForPapers;
  searchable_location_name: string;
  description: string;
  longitude: number;
  copyright: Copyright;
  location_name: string;
  type: string;
  organizer_name: string;
  latitude: number;
  code_of_conduct: string;
  version: Version;
  email: string;
  identifier: string;
  logo: string;
  timezone: string;
  creator: Creator;
  name: string;
  background_image: string;
  social_links: SocialLink[];
  topic: string;
  schedule_published_on: Date;
  id: number;
  organizer_description: string;
  state: string;
  ticket_url: string;
  privacy: string;
  event_url: string;
  has_session_speakers: boolean;
  tickets?: Ticket[];
}


