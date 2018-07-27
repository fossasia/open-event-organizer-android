package com.eventyay.organizer.data.faq;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FaqApi {

    @GET("events/{id}/faqs?include=event&fields[event]=id&page[size]=0")
    Observable<List<Faq>> getFaqs(@Path("id") long id);

    @POST("faqs")
    Observable<Faq> postFaq(@Body Faq faq);

    @DELETE("faqs/{id}")
    Completable deleteFaq(@Path("id") long id);
}
