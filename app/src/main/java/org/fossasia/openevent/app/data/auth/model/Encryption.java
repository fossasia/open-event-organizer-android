package org.fossasia.openevent.app.data.auth.model;

import android.content.Context;

import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Encryption {

    @Inject
    Context context;

    public String email;
    public String password;
}
