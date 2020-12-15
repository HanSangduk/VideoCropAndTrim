//package com.example.videocropandtrim.utils
//
///**
// * Skia EncodedOrigin
// *
// * @see http://sylvana.net/jpegcrop/exif_orientation.html
// */
//enum class EncodedOrigin{
//    top_left,
//    top_right,
//    bottom_right,
//    bottom_left,
//    left_top,
//    right_top,
//    right_bottom,
//    left_bottom,
//}
////{
////    public:
////    enum Value
////    {
////        top_left = 1,      // Default
////        top_right = 2,     // Reflected across y-axis
////        bottom_right = 3,  // Rotated 180
////        bottom_left = 4,   // Reflected across x-axis
////        left_top = 5,      // Reflected across x-axis, Rotated 90 CCW
////        right_top = 6,     // Rotated 90 CW
////        right_bottom = 7,  // Reflected across x-axis, Rotated 90 CW
////        left_bottom = 8,   // Rotated 90 CCW
////        default_origin = top_left
////    };
////    EncodedOrigin() = default;
////    explicit EncodedOrigin(Value v) : value(v) {}
////    void flip_horizontal();
////    void flip_vertical();
////    void rotate_cw();
////    void rotate_ccw();
////    bool is_should_swap_width_height() const
////{
////    return value >= EncodedOrigin::left_top;
////}
////    public:
////    Value value = Value::default_origin;
////};
//// ************************************************************************************************


//fun EncodedOrigin::flip_vertical()
//{
//    when (value)
//    {
//        case EncodedOrigin::top_left:
//        value = EncodedOrigin::bottom_left;
//        break;
//        case EncodedOrigin::top_right:
//        value = EncodedOrigin::bottom_right;
//        break;
//        case EncodedOrigin::bottom_right:
//        value = EncodedOrigin::top_right;
//        break;
//        case EncodedOrigin::bottom_left:
//        value = EncodedOrigin::top_left;
//        break;
//        case EncodedOrigin::left_top:
//        value = EncodedOrigin::left_bottom;
//        break;
//        case EncodedOrigin::right_top:
//        value = EncodedOrigin::right_bottom;
//        break;
//        case EncodedOrigin::right_bottom:
//        value = EncodedOrigin::right_top;
//        break;
//        case EncodedOrigin::left_bottom:
//        value = EncodedOrigin::left_top;
//        break;
//    }
//}
//fun EncodedOrigin::rotate_cw()
//{
//    // XXX: Encoded 된 origin 을 정의하므로, 역 방향의 이미지를 선택해야 그릴 때 회전되어서 나옵니다.
//    when (value)
//    {
//        case EncodedOrigin::top_left:
//        value = EncodedOrigin::right_top;
//        break;
//        case EncodedOrigin::top_right:
//        value = EncodedOrigin::right_bottom;
//        break;
//        case EncodedOrigin::bottom_right:
//        value = EncodedOrigin::left_bottom;
//        break;
//        case EncodedOrigin::bottom_left:
//        value = EncodedOrigin::left_top;
//        break;
//        case EncodedOrigin::left_top:
//        value = EncodedOrigin::top_right;
//        break;
//        case EncodedOrigin::right_top:
//        value = EncodedOrigin::bottom_right;
//        break;
//        case EncodedOrigin::right_bottom:
//        value = EncodedOrigin::bottom_left;
//        break;
//        case EncodedOrigin::left_bottom:
//        value = EncodedOrigin::top_left;
//        break;
//    }
//}



//fun EncodedOrigin::flip_horizontal()
//{
//    when (value)
//    {
//        case EncodedOrigin::top_left:
//        value = EncodedOrigin::top_right;
//        break;
//        case EncodedOrigin::top_right:
//        value = EncodedOrigin::top_left;
//        break;
//        case EncodedOrigin::bottom_right:
//        value = EncodedOrigin::bottom_left;
//        break;
//        case EncodedOrigin::bottom_left:
//        value = EncodedOrigin::bottom_right;
//        break;
//        case EncodedOrigin::left_top:
//        value = EncodedOrigin::right_top;
//        break;
//        case EncodedOrigin::right_top:
//        value = EncodedOrigin::left_top;
//        break;
//        case EncodedOrigin::right_bottom:
//        value = EncodedOrigin::left_bottom;
//        break;
//        case EncodedOrigin::left_bottom:
//        value = EncodedOrigin::right_bottom;
//        break;
//    }
//}

//fun  EncodedOrigin::rotate_ccw()
//{
//    // XXX: Encoded 된 origin 을 정의하므로, 역 방향의 이미지를 선택해야 그릴 때 회전되어서 나옵니다.
//    when (value)
//    {
//        case EncodedOrigin::top_left:
//        value = EncodedOrigin::left_bottom;
//        break;
//        case EncodedOrigin::top_right:            flip horizontal
//        value = EncodedOrigin::left_top;
//        break;
//        case EncodedOrigin::bottom_right:             180
//        value = EncodedOrigin::right_top;
//        break;
//        case EncodedOrigin::bottom_left:          180 ->  flip horizontal
//        value = EncodedOrigin::right_bottom;
//        break;
//        case EncodedOrigin::left_top:             90 ->  flip horizontal
//        value = EncodedOrigin::bottom_left;
//        break;
//        case EncodedOrigin::right_top:               270
//        value = EncodedOrigin::top_left;
//        break;
//        case EncodedOrigin::right_bottom:        270 -> flip horizontal
//        value = EncodedOrigin::top_right;
//        break;
//        case EncodedOrigin::left_bottom:            90
//        value = EncodedOrigin::bottom_right;
//        break;
//    }
//}
//
