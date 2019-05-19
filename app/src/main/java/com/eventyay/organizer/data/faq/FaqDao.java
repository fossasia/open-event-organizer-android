package com.eventyay.organizer.data.faq;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
public interface FaqDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertFaq(Faq faq);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertFaqList(List<Faq> faqList);

    @Query("DELETE FROM Faq WHERE id = :id")
    Completable deleteFaq(long id);

    @Query("SELECT * FROM Faq WHERE event = :eventId")
    Observable<List<Faq>> getAllFaqs(long eventId);
}
