import * as React from 'react';
import { useLocation } from 'react-router';

export default function BreadcrumbsGenerator() {
  const location = useLocation();

  // 현재 경로를 기반으로 breadcrumbs 생성
  const pathnames = location.pathname.split('/').filter((x) => Boolean(x)); // 빈 문자열 제거
  if (pathnames.length === 0) return []; // 경로가 비어있으면 빈 배열 반환

  let breadcrumbs: { path: string; title: string }[] = [];

  // 특정 페이지에 대한 네이밍 매핑 (타입 명확히 지정)
  const titleMap: Record<string, string> = {
    orders: "Orders",
    checkout: "Checkout",
    test: "Test"
  };

  // 첫 번째 경로명 처리 (Dashboard 제외)
  if (pathnames[0] !== 'dashboard') {
    breadcrumbs.push({
      path: `/${pathnames[0]}`,
      title: titleMap[pathnames[0]] ?? pathnames[0].charAt(0).toUpperCase() + pathnames[0].slice(1),
    });
  }

  // 나머지 경로 처리
  pathnames.forEach((value, index) => {
    if (!value) return;

    let path = `/${pathnames.slice(0, index + 1).join('/')}`;
    let title = titleMap[value] ?? (value.charAt(0).toUpperCase() + value.slice(1));

    // // 주문 상세보기 및 수정 페이지 처리
    // if (pathnames[0] === "orders") {
    //   if (!isNaN(Number(value))) {
    //     title = "";
    //     path = `/orders/${value}`;
    //   }
    //   if (value === "edit") {
    //     title = "change";
    //     breadcrumbs = [{ path: "/orders", title: "Orders" }, { path, title }];
    //     return;
    //   }
    // }

    // 첫 번째 경로는 이미 추가됨 (중복 방지)
    if (index !== 0) {
      breadcrumbs.push({ path, title });
    }
  });

  return breadcrumbs;
}
