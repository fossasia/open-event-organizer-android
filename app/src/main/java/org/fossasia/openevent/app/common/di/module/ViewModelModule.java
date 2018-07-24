package org.fossasia.openevent.app.common.di.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import org.fossasia.openevent.app.common.di.OrgaViewModelFactory;
import org.fossasia.openevent.app.core.auth.reset.ResetPasswordViewModel;
import org.fossasia.openevent.app.core.auth.login.LoginViewModel;
import org.fossasia.openevent.app.core.event.list.EventsViewModel;
import org.fossasia.openevent.app.core.event.create.CreateEventViewModel;
import org.fossasia.openevent.app.core.faq.create.CreateFaqViewModel;
import org.fossasia.openevent.app.core.main.EventViewModel;
import org.fossasia.openevent.app.core.orders.detail.OrderDetailViewModel;
import org.fossasia.openevent.app.core.main.OrganizerViewModel;
import org.fossasia.openevent.app.core.orders.list.OrdersViewModel;
import org.fossasia.openevent.app.core.settings.restriction.TicketSettingsViewModel;
import org.fossasia.openevent.app.core.share.ShareEventViewModel;
import org.fossasia.openevent.app.core.speaker.details.SpeakerDetailsViewModel;
import org.fossasia.openevent.app.core.speakerscall.create.CreateSpeakersCallViewModel;
import org.fossasia.openevent.app.core.sponsor.create.CreateSponsorViewModel;

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
    @ViewModelKey(TicketSettingsViewModel.class)
    public abstract ViewModel bindTicketSettingsViewModel(TicketSettingsViewModel ticketSettingsViewModel);

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(OrgaViewModelFactory factory);

}
