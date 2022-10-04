import React, { useState, FormEvent, ChangeEvent, useEffect } from 'react';
import Card from './CarouselCard';
import Slider from 'react-slick';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';
import NextArrow from './NextArrow';
import PrevArrow from './PrevArrow';
import { refrigeratorRecipe } from '../../api/main'
// css
import classes from './CarouselSimilar.module.scss';
import { CardRecipe } from './interface';
import { recipe } from '../refrigerator/interface';
import selectImg from '../../assets/select.jpg';

const CarouselSimilar= (props: {
  searchData:recipe[];
  getSearchData: () => recipe[];
}) => {
  const [searchResult, setSearchResult] = useState<recipe[]>([])
  const settings = {
    dots: false,
    infinite: true,
    speed: 500,
    slidesToShow: 3,
    slidesToScroll: 1,
    nextArrow: <NextArrow />,
    prevArrow: <PrevArrow />,
    // dotsClass: "custom-dots"
  };

  useEffect(() => {
    const value = props.getSearchData()
    setSearchResult(value)
    console.log(searchResult, 'check carousel---')
  }, [searchResult])

  const cardList = searchResult;

  return (
    <>
      <section className={classes.carousel}>
        {cardList.length === 0 ? 
          <img src={selectImg} alt="재료를 선택해 주세요" />
          :<h2>있음</h2>
        }
        <Slider {...settings}>
          {cardList.map((card:recipe)=>(<Card card={card} key={card.id}/>))}
        </Slider> 
      </section>
    </>
  );
};

export default CarouselSimilar;
