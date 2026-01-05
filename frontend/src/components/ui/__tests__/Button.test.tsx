import React from 'react'
import { render, screen, fireEvent } from '@testing-library/react'
import { Button, getButtonClasses, buttonVariants, buttonSizes } from '../Button'

describe('Button', () => {
    describe('getButtonClasses', () => {
        it('should return primary variant classes by default', () => {
            const classes = getButtonClasses()
            expect(classes).toContain('bg-primary')
            expect(classes).toContain('text-primary-foreground')
        })

        it('should return secondary variant classes', () => {
            const classes = getButtonClasses('secondary')
            expect(classes).toContain('bg-secondary')
        })

        it('should return ghost variant classes', () => {
            const classes = getButtonClasses('ghost')
            expect(classes).toContain('hover:bg-accent')
        })

        it('should return outline variant classes', () => {
            const classes = getButtonClasses('outline')
            expect(classes).toContain('border')
            expect(classes).toContain('bg-transparent')
        })

        it('should return destructive variant classes', () => {
            const classes = getButtonClasses('destructive')
            expect(classes).toContain('bg-destructive')
        })

        it('should apply correct size classes', () => {
            const smClasses = getButtonClasses('primary', 'sm')
            expect(smClasses).toContain('h-8')
            expect(smClasses).toContain('px-3')

            const mdClasses = getButtonClasses('primary', 'md')
            expect(mdClasses).toContain('h-10')
            expect(mdClasses).toContain('px-4')

            const lgClasses = getButtonClasses('primary', 'lg')
            expect(lgClasses).toContain('h-12')
            expect(lgClasses).toContain('px-8')

            const iconClasses = getButtonClasses('primary', 'icon')
            expect(iconClasses).toContain('h-10')
            expect(iconClasses).toContain('w-10')
        })

        it('should merge custom className', () => {
            const classes = getButtonClasses('primary', 'md', 'custom-class')
            expect(classes).toContain('custom-class')
        })
    })

    describe('Button component', () => {
        it('should render button with children', () => {
            render(<Button>Click me</Button>)
            expect(screen.getByRole('button', { name: 'Click me' })).toBeInTheDocument()
        })

        it('should apply primary variant by default', () => {
            render(<Button>Primary Button</Button>)
            const button = screen.getByRole('button')
            expect(button.className).toContain('bg-primary')
        })

        it('should apply specified variant', () => {
            render(<Button variant="secondary">Secondary Button</Button>)
            const button = screen.getByRole('button')
            expect(button.className).toContain('bg-secondary')
        })

        it('should apply specified size', () => {
            render(<Button size="lg">Large Button</Button>)
            const button = screen.getByRole('button')
            expect(button.className).toContain('h-12')
        })

        it('should handle click events', () => {
            const handleClick = jest.fn()
            render(<Button onClick={handleClick}>Clickable</Button>)

            fireEvent.click(screen.getByRole('button'))

            expect(handleClick).toHaveBeenCalledTimes(1)
        })

        it('should forward ref to button element', () => {
            const ref = React.createRef<HTMLButtonElement>()
            render(<Button ref={ref}>With Ref</Button>)

            expect(ref.current).toBeInstanceOf(HTMLButtonElement)
            expect(ref.current?.tagName).toBe('BUTTON')
        })

        it('should pass through additional props', () => {
            render(<Button disabled type="submit" data-testid="custom-button">Submit</Button>)

            const button = screen.getByTestId('custom-button')
            expect(button).toBeDisabled()
            expect(button).toHaveAttribute('type', 'submit')
        })

        it('should apply custom className alongside variant classes', () => {
            render(<Button className="my-custom-class">Custom Class</Button>)
            const button = screen.getByRole('button')

            expect(button.className).toContain('my-custom-class')
            expect(button.className).toContain('bg-primary') // Still has default variant
        })

        it('should have proper accessibility attributes', () => {
            render(<Button aria-label="Close dialog">X</Button>)

            expect(screen.getByRole('button')).toHaveAccessibleName('Close dialog')
        })
    })

    describe('buttonVariants', () => {
        it('should have all expected variants defined', () => {
            expect(buttonVariants).toHaveProperty('primary')
            expect(buttonVariants).toHaveProperty('secondary')
            expect(buttonVariants).toHaveProperty('ghost')
            expect(buttonVariants).toHaveProperty('outline')
            expect(buttonVariants).toHaveProperty('destructive')
        })
    })

    describe('buttonSizes', () => {
        it('should have all expected sizes defined', () => {
            expect(buttonSizes).toHaveProperty('sm')
            expect(buttonSizes).toHaveProperty('md')
            expect(buttonSizes).toHaveProperty('lg')
            expect(buttonSizes).toHaveProperty('icon')
        })
    })
})
