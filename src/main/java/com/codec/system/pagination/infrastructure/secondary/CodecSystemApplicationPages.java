package com.codec.system.pagination.infrastructure.secondary;

import codec.error.domain.Assert;
import com.codec.system.pagination.domain.CodecSystemApplicationPage;
import com.codec.system.pagination.domain.CodecSystemApplicationPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.function.Function;

public final class CodecSystemApplicationPages {

  private CodecSystemApplicationPages() {}

  public static Pageable from(CodecSystemApplicationPageable pagination) {
    return from(pagination, Sort.unsorted());
  }

  public static Pageable from(CodecSystemApplicationPageable pagination, Sort sort) {
    Assert.notNull("pagination", pagination);
    Assert.notNull("sort", sort);

    return PageRequest.of(pagination.page(), pagination.pageSize(), sort);
  }

  public static <S, T> CodecSystemApplicationPage<T> from(Page<S> springPage, Function<S, T> mapper) {
    Assert.notNull("springPage", springPage);
    Assert.notNull("mapper", mapper);

    return CodecSystemApplicationPage
      .builder(springPage.getContent().parallelStream().map(mapper).toList())
      .currentPage(springPage.getNumber())
      .pageSize(springPage.getSize())
      .build();
  }
}
