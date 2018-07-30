package com.eventyay.organizer.common.di.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.eventyay.organizer.common.di.OrgaViewModelFactory;
import com.eventyay.organizer.core.auth.reset.ResetPasswordViewModel;
import com.eventyay.organizer.core.auth.login.LoginViewModel;
import com.eventyay.organizer.core.auth.signup.SignUpViewModel;
import com.eventyay.organizer.core.event.list.EventsViewModel;
import com.eventyay.organizer.core.event.create.CreateEventViewModel;
import com.eventyay.organizer.core.faq.create.CreateFaqViewModel;
import com.eventyay.organizer.core.main.EventViewModel;
import com.eventyay.organizer.core.orders.detail.OrderDetailViewModel;
import com.eventyay.organizer.core.main.OrganizerViewModel;
import com.eventyay.organizer.core.orders.list.OrdersViewModel;
import com.eventyay.organizer.core.organizer.update.UpdateOrganizerInfoViewModel;
import com.eventyay.organizer.core.settings.restriction.TicketSettingsViewModel;
import com.eventyay.organizer.core.share.ShareEventViewModel;
import com.eventyay.organizer.core.speaker.details.SpeakerDetailsViewModel;
import com.eventyay.organizer.core.speakerscall.create.CreateSpeakersCallViewModel;
import com.eventyay.organizer.core.sponsor.create.CreateSponsorViewModel;
import com.eventyay.organizer.core.ticket.create.CreateTicketViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    public abstract ViewModel bindLoginViewModel(LoginViewModel loginViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ResetPasswordViewModel.class)
    public abstract ViewModel bindResetPasswordViewModel(ResetPasswordViewModel resetPasswordViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SpeakerDetailsViewModel.class)
    public abstract ViewModel bindSpeakerDetailsViewModel(SpeakerDetailsViewModel speakerDetailsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(OrdersViewModel.class)
    public abstract ViewModel bindOrdersViewModel(OrdersViewModel ordersViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CreateSpeakersCallViewModel.class)
    public abstract ViewModel bindCreateSpeakersCallViewModel(CreateSpeakersCallViewModel createSpeakersCallViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ShareEventViewModel.class)
    public abstract ViewModel bindShareEventViewModel(ShareEventViewModel shareEventViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(OrderDetailViewModel.class)
    public abstract ViewModel bindOrderDetailViewModel(OrderDetailViewModel orderDetailViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(OrganizerViewModel.class)
    public abstract ViewModel bindOrganizerViewModel(OrganizerViewModel organizerViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(EventViewModel.class)
    public abstract ViewModel bindEventViewModel(EventViewModel eventViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(EventsViewModel.class)
    public abstract ViewModel bindEventsViewModel(EventsViewModel eventsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CreateEventViewModel.class)
    public abstract ViewModel bindCreateEventViewModel(CreateEventViewModel eventViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CreateFaqViewModel.class)
    public abstract ViewModel bindCreateFaqViewModel(CreateFaqViewModel faqViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CreateSponsorViewModel.class)
    public abstract ViewModel bindCreateSponsorViewModel(CreateSponsorViewModel sponsorViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CreateTicketViewModel.class)
    public abstract ViewModel bindCreateTicketViewModel(CreateTicketViewModel ticketViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SignUpViewModel.class)
    public abstract ViewModel bindSignUpViewModel(SignUpViewModel signUpViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(TicketSettingsViewModel.class)
    public abstract ViewModel bindTicketSettingsViewModel(TicketSettingsViewModel ticketSettingsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UpdateOrganizerInfoViewModel.class)
    public abstract ViewModel bindUpdateOrganizerViewModel(UpdateOrganizerInfoViewModel ticketSettingsViewModel);

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(OrgaViewModelFactory factory);

}
