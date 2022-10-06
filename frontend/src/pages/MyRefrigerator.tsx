// custom component
import { useEffect, useState } from 'react';
// import { useSelector, useDispatch } from 'react-redux';
// import setIngredients from '../redux/slice/refrigerator';

import { getRefrigerator } from '../api/myrefrigerator';
import { ingredient, recipe } from '../components/refrigerator/interface';
import Refrigerator from '../components/refrigerator/Refrigerator';
import RefrigeratorBox from '../components/refrigerator/RefrigeratorBox';
import CarouselSimilar from '../components/mainpage/CarouselSimilar';
import { useSelector } from 'react-redux';

// css
import classes from './MyRefrigerator.module.scss';

export default function MyRefrigerator() {
  const [myIngredient, setMyIngredient] = useState<ingredient[]>([]);
  // const [selectIngre, setSelectIngre] = useState<number[]>([]);
  // const [searchData, setSearchData] = useState<never[]>([]);
  const [isMyRefrigListUpdate, setIsMyRefrigeListUpdate] = useState(false);
  const selectedIngredientList = useSelector(
    (state: any) => state.refrigerator.cookingIngredientList
  );

  // search 보내게 string으로 변환
  // const ingre = (newValue: number[]) => {
  //   return newValue.join('-');
  // };
  const handleMyRefrigeListUpdate = () => {
    setIsMyRefrigeListUpdate((prev) => !prev);
  };
  // 재료 선택 & 검색
  // const searchIngre = (data: number) => {
  //   if (searchData.length === 0) {
  //     setSelectIngre([data]);
  //     (async () => {
  //       const tmp = ingre(selectIngre);
  //       const data = await searchRecipe(tmp);
  //       setSearchData(data);
  //     })();
  //   } else {
  //     setSelectIngre([...selectIngre, data]);
  //     (async () => {
  //       const tmp = ingre(selectIngre);
  //       const data = await searchRecipe(tmp);
  //       setSearchData(data);
  //     })();
  //   }
  // };
  // const getSearchData = () => {
  //   return searchData;
  // };
  // const deleteIngre = (data: number) => {
  //   const newValue = selectIngre.filter((id) => id !== data);
  //   setSelectIngre(newValue);
  //   if (newValue.length > 0) {
  //     (async () => {
  //       const tmp = ingre(selectIngre);
  //       const data = await searchRecipe(tmp);
  //       setSearchData(data);
  //     })();
  //   } else {
  //     setSearchData([]);
  //   }
  // };

  useEffect(() => {
    (async () => {
      const data = await getRefrigerator();
      setMyIngredient(data);
    })();
  }, [isMyRefrigListUpdate]);
  return (
    <>
      <div className={classes.header}>
        <div>내 냉장고</div>
      </div>

      <div className={classes.wrapper}>
        <div className={classes.refrigerator}>
          <RefrigeratorBox
            item={myIngredient}
            onAddItem={handleMyRefrigeListUpdate}
          />
        </div>
        <div className={classes.ingredientlistContainer}>
          <div>현재 선택한 재료</div>
          <Refrigerator selectedItemList={selectedIngredientList} />
        </div>
        <div className={classes.foodlistContainer}>
          <div>선택한 재료로 만들 수 있는 음식</div>
          <CarouselSimilar selectedItemList={selectedIngredientList} />
        </div>
      </div>
    </>
  );
}
