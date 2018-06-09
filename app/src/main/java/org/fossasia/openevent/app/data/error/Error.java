package org.fossasia.openevent.app.data.error;

import org.fossasia.openevent.app.utils.Utils;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Error {

    public String title;
    public String detail;
    public String pointer;

    @Override
    public String toString() {

        if (Utils.isEmpty(title)) {
            if (!Utils.isEmpty(detail)) {
                if (Utils.isEmpty(pointer)) {
                    return detail;
                } else {
                    return detail + " - " + pointer;
                }
            }
        } else {
            if (Utils.isEmpty(pointer)) {
                return title + ": " + detail;
            } else {
                return title + ": " + detail + " - " + pointer;
            }
        }

        return null;
    }
}
