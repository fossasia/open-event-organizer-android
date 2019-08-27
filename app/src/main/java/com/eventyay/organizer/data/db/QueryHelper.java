package com.eventyay.organizer.data.db;

import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;
import com.raizlabs.android.dbflow.sql.language.property.Property;
import com.raizlabs.android.dbflow.sql.queriable.ModelQueriable;
import io.reactivex.Observable;
import java.util.ArrayList;
import java.util.List;

public class QueryHelper<R> {
    private final List<IProperty> properties = new ArrayList<>();
    private IProperty method;

    private From<R> from;
    private Where<R> where;

    public QueryHelper<R> select(IProperty... properties) {
        for (IProperty property : properties) this.properties.add(property.withTable());
        return this;
    }

    public QueryHelper<R> from(Class<R> source) {
        if (method != null) properties.add(method);

        from = SQLite.select(properties.toArray(new IProperty[properties.size()])).from(source);
        return this;
    }

    public QueryHelper<R> sum(IProperty property, String alias) {
        return method(Method.sum(property.withTable()), alias);
    }

    public QueryHelper<R> method(Method method, String alias) {
        this.method = method.as(alias);
        return this;
    }

    public QueryHelper<R> equiJoin(Class joinTable, Property one, Property two) {
        from.innerJoin(joinTable).on(one.withTable().eq(two.withTable()));
        return this;
    }

    public QueryHelper<R> where(SQLOperator... operators) {
        where = from.where(operators);
        return this;
    }

    public QueryHelper<R> and(SQLOperator operator) {
        where.and(operator);
        return this;
    }

    public QueryHelper<R> group(IProperty by) {
        if (where == null) where = from.groupBy(by.withTable());
        else where = where.groupBy(by.withTable());
        return this;
    }

    public ModelQueriable<R> build() {
        if (where == null) return from;

        return where;
    }

    public Observable<R> toObservable() {
        return RXSQLite.rx(build()).queryList().flattenAsObservable(items -> items);
    }

    public Observable<Long> count() {
        return Observable.fromCallable(() -> build().longValue());
    }

    public <T> Observable<T> toCustomObservable(Class<T> to) {
        return RXSQLite.rx(build())
                .queryResults()
                .map(rCursorResult -> rCursorResult.toCustomListClose(to))
                .flatMapObservable(Observable::fromIterable);
    }
}
