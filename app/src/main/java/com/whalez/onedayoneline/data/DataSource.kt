package com.whalez.onedayoneline.data

import com.whalez.onedayoneline.models.DiaryPost

class DataSource {

    companion object{
        fun createDataSet(): ArrayList<DiaryPost>{
            val list = ArrayList<DiaryPost>()
            list.add(
                DiaryPost(
                    "한국외대 컴퓨터공학과에 입학했다.",
                    "2020.01.01 수"
                )
            )
            list.add(
                DiaryPost(
                    "안드로이드를 배우기 시작했는데 너무 행복하다.",
                    "2020.01.02 목"
                )
            )
            list.add(
                DiaryPost(
                    "드디어 학교를 졸업했다. 기분이 마냥 행복하지만은 않다ㅎㅎㅎ",
                    "2020.01.03 금"
                )
            )
            list.add(
                DiaryPost(
                    "인턴 서류 전형에 합격했다. 내일은 면접이다! 준비를 잘 해야겠다!",
                    "2020.01.04 토"
                )
            )
            list.add(
                DiaryPost(
                    "면접에서 떨어졌지만, 내가 부족한게 뭔지 알게되었고 많은걸 얻을 수 있는 좋은 기회였다!",
                    "2020.01.05 일"
                )
            )
            return list
        }
    }
}